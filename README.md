# Google Analytics export to CSV #

## Overview ##
Google-Analytics-export-to-CSV is a simple, command-line tool for exporting data out of Google Analytics and writing it to a CSV file. Please note that Google Analytics only lets you export **aggregate data** - if you need access to raw clickstream data, please check out the [SnowPlow](https://github.com/snowplow/snowplow) sister project.

Any query that can be executed against Google Analytics data via the API can be run using the tool. Each query is stored in its own XML files which details the metrics, dimensions, filters and segments are for each specific query. A number of sample queries (we call them "recipes") are included with the distribution.

The tool is built in Scala. Dependencies are managed via SBT. It is compiled to a single JAR (using ProGuard). 

## Quick start ##
A compiled version of Google-Analytics-export-to-CSV can be downloaded directly from [here](https://github.com/datascience/google-analytics-export-to-csv/downloads) - click the "ga2csv-download.zip" link to initiate the download. 

Unzip the downloaded file on your local disc. It contains:

1.	The executable JAR file, `ga2csv-0.1.jar` can be run directly (details below)
2.	A set of sample "recipes" (really queries) in `/recipes` folder
3.	A step-by-step guide to using the tool called `Using Google-Analytics-export-to-CSV.pdf`

Downloading the compiled version means you can get started crunching data straight away, without having to compile any source code first.


## How to Run ##
Running the export is a 2 step process:

1.	Creating the query XML
2.	Executing the query

### 1. Creating the query XML ###
The query XML takes the form of 

```xml
<?xml version="1.0" encoding="UTF-8"?>
<orderly xmlns:xlink="http://www.w3.org/1999/xlink">
<configuration>
<query>
    <tableId>{ENTER YOUR TABLE ID}</tableId>
    <dimensions>{ENTER THE DIMENSIONS}</dimensions>
    <metrics>{ENTER THE METRICS}}</metrics>
    <segments>{OPTIONAL: ENTER THE SEGMENTS}</segments>
    <filters>{OPTIONAL: ENTER THE FILTERS}</filters>
    <sort>{OPTIONAL: ENTER SORT}</sort>
    <start-date>{ENTER START DATE}</start-date>
    <end-date>{ENTER START DATE}</end-date>
    <start-index>{OPTIONAL: ENTER START DATE}</start-index>
    <max-results>{OPTIONAL: ENTER END DATE}</max-results>
</query>
<login>
    <userName>{ENTER YOUR GOOGLE ANALYTICS USERNAME HERE}</userName>
    <password>{ENTER YOUR GOOGLE ANALYTICS PASSWORD HERE}</password>
</login>
<app>
    <appName>ga-quick-data-grabber</appName>
    <url>https://www.google.com/analytics/feeds/data</url>
</app>
</configuration>
</orderly>
```

Simply enter the relevant parameters into the XML and save the result. An example of a completed XML (without a valid username and password) is shown below:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<orderly xmlns:xlink="http://www.w3.org/1999/xlink">
<configuration>
<query>
    <tableId>{ENTER YOUR TABLE ID HERE}</tableId>
<dimensions>ga:date,ga:visitCount,ga:source,ga:medium,ga:keyword,ga:adContent,ga:country</dimensions>
    <metrics>ga:visitors,ga:newVisits,ga:visits,ga:pageviews,ga:totalEvents,ga:transactions,ga:itemQuantity,ga:transactionRevenue,ga:timeOnSite,ga:bounces</metrics>
    <segments></segments>
    <filters></filters>
    <sort></sort>
    <start-date>2011-11-01</start-date>
    <end-date>2012-01-27</end-date>
    <start-index></start-index>
    <max-results></max-results>
</query>
<login>
    <userName>{ENTER YOUR GOOGLE ANALYTICS USERNAME HERE}</userName>
    <password>{ENTER YOUR GOOGLE ANALYTICS PASSWORD HERE}</password>
</login>
<app>
    <appName>google-analytics-export-to-csv</appName>
    <url>https://www.google.com/analytics/feeds/data</url>
</app>
</configuration>
</orderly>
```

Google provides an excellent [Data Feed Query Explorer](http://code.google.com/apis/analytics/docs/gdata/gdataExplorer.html) which makes putting together and testing queries before executing them in this tool very easy.

### 2. Executing the query ###
Assuming

1.	You have saved the query down as `new-query.xml` and
2.	are executing the query using the single JAR file (either `ga2csv-0.1.jar`, downloaded directly from the ZIP, or built yourself, following the instructions below)

Execute the following command at the command line:

	java -jar ga2csv-0.1.jar new-query.xml output-data.csv 


A step-by-step tutorial to developing and executing queries against using the tool can be found [here](http://www.keplarllp.com/blog/2012/01/using-google-analytics-export-to-csv-a-step-by-step-guide).


## Example Recipes ##
A number of example "recipes" (i.e. queries) are included with the distribution.

1.	`traffic-and-sales-growth-over-time.xml`: a record of the number of visitors, visits, page views, events, and transactions by day, split by the source of traffic. This is a good aggregate of data for understanding the drivers of visitor and sales growth over time
2.	`spend-by-adword-campaign`: a record of the page views, visits, transactions, transaction revenue by adwords campaign (broken down by keywords, ad content and destination URL). Useful for understanding the ROI on individual campaigns, and the impact of different campaigns in driving visitor and revenue growth
3.	`funnel-by-keyword`: a record of the page views of specific pages broken down by the AdWords campaign that drove the user to the site. This is useful for understanding the differences in behaviour (defined as which pages were visited and the number of pages) by users coming to the site from different AdWords campaigns. Specifically, it enables the analyst to define different "funnels" (i.e. specific sets of pages visited as part of a session) in her analytics tool, and see what % of visitors by AdWords campaign progressed through those funnels
4.	`visitors-by-page`: a simple query to understand the distribution in page views by page

We intend to show how the above recipes can be used as part of sophisticated web analytics in the future on the [Keplar blog](http://www.keplarllp.com/blog)


## Downloading and installing the source code
Google-Analytics-to-CSV has been written in Scala with dependencies managed in SBT. We've used ProGuard to compile the project into a single JAR.

To download the source code:

	$ git clone git@github.com:datascience/google-analytics-export-to-csv.git
	$ cd google-analytics-export-to-csv
	$ sbt
	$ sbt> package
	$ sbt> proguard

The all-in-one JAR should be created in

	/target/scala-2.9.1/google-analytics-export-to-cxv_2.9.1-0.1.min.jar

## More information ##
We have written a set of step-by-step tutorials to explain how this tool can be used in conjunction with a business intelligence like Tableau to provide insight that goes beyond what is possible with Google Analytics via the web interface.

These tutorials have been published as a blog post series on the [Keplar blog](http://www.keplarllp.com/blog):

* [Introducing Google-Analytics-export-to-CSV] [blogintro]
* [Installing Google-Analytics-export-to-CSV] [bloginstall]
* [Using Google-Analytics-export-to-CSV: a step-by-step guide] [bloguse]
* [Using Tableau and Google Analytics to analyse the drivers of growth in online retail] [blogtableau] 

## Contact ##

For questions, comments, feature requests, email yali.sassoon@keplarllp.com

## Google API documentation

1. Google [Data Feed Query Explorer](http://code.google.com/apis/analytics/docs/gdata/gdataExplorer.html)
2.	Google [Core Reporting API documentation](http://code.google.com/apis/analytics/docs/gdata/v3/gdataGettingStarted.html)

## Introducing SnowPlow ##

Google Analytics export to CSV empowers data analysts by freeing their data from the constraints of the Google Analytics web UI, so they can analyse it in much more flexible ways using other tools. (E.g. Excel, R, Tableau.)

A related project that may be of interest for data analysts interested in this project is [SnowPlow](https://github.com/snowplow/snowplow). SnowPlow is a web analytics platform that provides analysts with access to customer-level and event-level data, so they can perform much more sophisticated analyses than are possible using data fetched from Google Analytics. (Which is always aggregated.)


## Copyright and License

Google-Analytics-export-to-CSV is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

[blogintro]: http://www.keplarllp.com/blog/2012/01/introducing-google-analytics-export-to-csv-a-fast-simple-way-to-get-your-google-analytics-data-into-your-favourite-analytics-programme
[bloginstall]: http://www.keplarllp.com/blog/2012/01/google-analytics-export-to-csv
[bloguse]: http://www.keplarllp.com/blog/2012/01/using-google-analytics-export-to-csv-a-step-by-step-guide
[blogtableau]: http://www.keplarllp.com/blog/2012/02/using-tableau-and-google-analytics-to-analyse-the-drivers-of-growth-in-online-retail