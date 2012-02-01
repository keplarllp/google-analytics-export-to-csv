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
import java.io.File

// Scala
import xml.XML
import collection.mutable.ListBuffer

// Google Analytics GData library
import com.google.gdata.data.analytics.DataFeed
import com.google.gdata.client.analytics.{DataQuery, AnalyticsService}

/**
 * Ga2CsvMain is the command-line tool which wraps our GoogleAnalyticsQuery.
 */
object Ga2CsvMain {

  def main(args: Array[String]){

    // -------------------------------------------------------------------------------------------------------------------
    // Handle command line arguments
    // -------------------------------------------------------------------------------------------------------------------

    // Check that two arguments have been supplied i.e. an inputFile and an outputFile
    checkArgs(args)
    val configFile = args(0)
    val outputFile = args(1)

    // Throw exception if output file already exists
    checkExists(outputFile)

    // -------------------------------------------------------------------------------------------------------------------
    // Execute Google Analytics export
    // -------------------------------------------------------------------------------------------------------------------

    // Initialize and configure the exporter
    val gae = new GoogleAnalyticsExporter(configFile)

    // Setup and run query, dumping to outputFile
    gae.exportData(outputFile)
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Helper methods for command-line handling
  // -------------------------------------------------------------------------------------------------------------------

  /**
   * Check we're not overwriting an existing file
   */
  private def checkExists(filename: String) {
    val outputFile = new File(filename)
    if (outputFile.exists) throw new IllegalArgumentException("Error: Output file %s already exists".format(filename))
  }

  /**
   * Check that the number of arguments supplied is correct
   * (i.e. 2, one for input file, one for output file)
   */
  def checkArgs(args: Array[String]) {
    if (args.length != 2) throw new IllegalArgumentException("Two arguments required, but %s supplied. Please specific input and output files".format(args.length))
  }
}