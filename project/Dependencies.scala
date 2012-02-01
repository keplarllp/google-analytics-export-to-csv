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
import sbt._

object Dependencies {
  val resolutionRepos = Seq(
    "Mandubian Repository" at "http://mandubian-mvn.googlecode.com/svn/trunk/mandubian-mvn/repository/",
    ScalaToolsSnapshots
  )

  object V {
    val gdata     = "1.41.5"
  }

  object Ga2Csv {

    // Only dependency is the Gdata libraries for Google Analytics
    // Provided by mandubian-mvn: http://code.google.com/p/mandubian-mvn/wiki/AvailableVersions
    val gdataCore      = "com.google.gdata" % "gdata-core-1.0"      % V.gdata
    val gdataAnalytics = "com.google.gdata" % "gdata-analytics-2.1" % V.gdata
  }
}
