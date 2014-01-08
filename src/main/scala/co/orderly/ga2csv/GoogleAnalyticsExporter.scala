/*
 * Copyright (c) 2012 Orderly Ltd. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package co.orderly.ga2csv

// Java
import java.net.URL
import java.io.{PrintWriter, File}

// Scala
import xml.{XML, Elem}
import collection.mutable.ListBuffer
import scala.collection.JavaConversions._

// Google Analytics GData library
import com.google.gdata.client.analytics.AnalyticsService
import com.google.gdata.client.analytics.DataQuery
import com.google.gdata.data.analytics.{DataEntry, DataFeed}

/**
 * GoogleAnalyticsExporter is a collection of functions to perform the export of
 * a given set of dimensions and metrics from Google Analytics into a CSV.
 */
class GoogleAnalyticsExporter(configFile: String) {

  // -------------------------------------------------------------------------------------------------------------------
  // Constructor
  // -------------------------------------------------------------------------------------------------------------------

  // Load the configuration file as XML
  protected val configXML = XML.loadFile(configFile)

  // Initialize the AnalyticsService
  protected val analyticsService: AnalyticsService = initAnalytics(configXML)

  // Initialize the DataQuery
  protected val (analyticsQuery, maxResults) = initQuery(configXML)

  // Configurable separator, comma by default
  protected val separator = (configXML \ "configuration" \ "app" \ "separator" ).text match {
    case "" => ","
    case s : String => s
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Builders for the Google AnalyticsService and DataFeed
  // -------------------------------------------------------------------------------------------------------------------

  /**
   * Method to initialize the Analytics service
   *
   */
  protected def initAnalytics(configXML: Elem): AnalyticsService = {

    // Grab config variables from XML
    val appName = (configXML \ "configuration" \ "app" \ "appName").text
    val userName = (configXML \ "configuration" \ "login" \ "userName").text
    val password = (configXML \ "configuration" \ "login" \ "password").text

    // Now initialise the Google Analytics service
    val as = new AnalyticsService(appName)

    // Set authentication
    as.setUserCredentials(userName, password)

    as // Return new AnalyticsService
  }

  /**
   * Method for building a DataQuery given a set of query parameters
   */
  protected def initQuery(configXML: Elem): Tuple2[DataQuery, Int] = {

    // Grab config variables from XML
    val tableId = (configXML \ "configuration" \ "query" \ "tableId").text
    val dimensions = (configXML \ "configuration" \ "query" \ "dimensions").text
    val metrics = (configXML \ "configuration" \ "query" \ "metrics").text
    val filters = (configXML \ "configuration" \ "query" \ "filters").text
    val sort = (configXML \ "configuration" \ "query" \ "sort").text
    val startDate = (configXML \ "configuration" \ "query" \ "start-date").text
    val endDate = (configXML \ "configuration" \ "query" \ "end-date").text
    val maxResults = (configXML \ "configuration" \ "query" \ "max-results").text
    val url = (configXML \ "configuration" \ "app" \ "url").text
    val userIp = (configXML \ "configuration" \ "query" \ "userIp").text

    // Build a new GA query using the specified URL
    val query = new DataQuery(new URL(url))

    // All queries have these four parameters
    query.setStartDate(startDate)
    query.setEndDate(endDate)
    query.setMetrics(metrics)
    query.setIds(tableId)
   
    // Populate optional parameters if specified
    if (!dimensions.isEmpty) query.setDimensions(dimensions)
    if (!filters.isEmpty) query.setFilters(filters)
    if (!sort.isEmpty) query.setSort(sort)
    if (!userIp.isEmpty) query.setStringCustomParameter("userIp", userIp)

    // Check the maximum cap per run
    val mx =
      if (!maxResults.isEmpty) {
        maxResults.toInt
      } else { // If no maxResults set, make it the max GA allows
        10000
      }
    query.setMaxResults(mx)

    // Return the query
    (query, mx)
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Methods to execute query and export the data
  // -------------------------------------------------------------------------------------------------------------------

  /**
   * Run this to export the data using the instance's analyticsQuery
   * @param outputFile The name of file to write to
   */
  def exportData(outputFile: String) {

    // Run executeQuery as many times as required, starting with an
    // empty ListBuffer
    val allData: List[DataFeed] = executeQuery()

    // Save results
    writeResultsToFile(allData, outputFile)
  }

  /**
   * A simple wrapper around Google Analytics service getFeed.
   * We call this executeQueryRun because we may need to execute
   * it multiple times (multiple runs) to complete the export of
   * data that matches this query.
   */
  protected def executeQueryRun(query: DataQuery): DataFeed =
    analyticsService.getFeed(query.getUrl, classOf[DataFeed])

  /**
   *
   * Runs the query as many times as necessary to export all
   * the data. This is because Google Analytics sets a per-
   * query cap, typically of 10,000 rows per run.
   */
  protected def executeQuery(allData: List[DataFeed] = Nil): List[DataFeed] = allData match {

    // Our starting case
    case Nil => executeQuery(List(executeQueryRun(analyticsQuery)))

    // Not the first run
    case head :: _ => head.getEntries().size match {

      // If we have retrieved the maximum possible results, run again
      case `maxResults` => {
        // Increment query start point
        analyticsQuery.setStartIndex(analyticsQuery.getStartIndex + maxResults)
        // Rerun query
        executeQuery(executeQueryRun(analyticsQuery) :: allData)
      }
      // We're done
      case _ => allData.reverse
    }
  }

  // -------------------------------------------------------------------------------------------------------------------
  // File writing code
  // -------------------------------------------------------------------------------------------------------------------

  /**
   * Writes the results to file
   */
  private def writeResultsToFile(entryBatches: List[DataFeed], outputFile:String) {
    val entries = entryBatches(0).getEntries

    // create a new file with the filename specified when the script was run
    val output = new PrintWriter(new File(outputFile))

    // 1st, print header row detailing contents of each column (1st dimensions, 2nd metrics)
    val r = entries(0) // take a single result to use to fetch details for header row
    val dimensions = r.getDimensions

    val headerLine = new StringBuilder
    // Write titles of dimensions
    if (!dimensions.isEmpty) { dimensions.map(dimension => (headerLine ++= (dimension.getName + separator + " "))) }
    val metrics = r.getMetrics
    // Write title of metrics
    metrics.map(metric => (headerLine ++= (metric.getName + separator)))
    // Now remove trailing ",", then enter a LF, write the line to the file
    output.write( ((headerLine.dropRight(1)) ++= ("\n")).toString )

    // Iterate over the list of batches
    // TODO: change batch language to run
    for (batch <- entryBatches){
      // Iterate over every entry batch
      for (entry:DataEntry <- batch.getEntries) {
        val dataLine = new StringBuilder
        // dimension values
        val dimensions = entry.getDimensions
        if (!dimensions.isEmpty) { dimensions.map(dimension => (dataLine ++= (dimension.getValue + separator))) }
        // metric values
        val metrics = entry.getMetrics
        metrics.map(metric => (dataLine ++= (metric.getValue + separator)))
        // Now remove trailing ",", then enter a LF, write the line to the file
        output.write( (dataLine.dropRight(1) ++= ("\n")).toString )
      }
    }
    output.close()
  }
}

