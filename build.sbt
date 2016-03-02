import sbt._
import sbt.Keys._
import ScoverageSbtPlugin._

lazy val root = project.in(file(".")).
  aggregate(turtleparserJS, turtleparserJVM).
  settings(
    publish := {},
    publishLocal := {}
  )

lazy val turtleparser = crossProject.
  crossType(CrossType.Full).
  settings(
    name := "turtleparser",
    version := "0.0.5",
    scalaVersion := "2.11.7", 
    organization := "es.weso",
	libraryDependencies ++= Seq(
     "org.scalatest" %%% "scalatest" % "3.0.0-M15" % "test"
	)
  ).
  jvmSettings(
    name := name.value + "-jvm",
	libraryDependencies ++= Seq(
      "es.weso" % "stateparser_2.11" % "0.1.1"
	, "es.weso" % "srdf-jvm_2.11" % "0.0.5"
    , "commons-configuration" % "commons-configuration" % "1.7"
    , "com.typesafe" % "config" % "1.2.0"
    , "org.apache.jena" % "jena-arq" % "2.13.0" % "test"
    , "junit" % "junit" % "4.10" % "test"
    ),
	licenses += ("MPL-2.0", url("http://opensource.org/licenses/MPL-2.0"))
  ).
  jsSettings(
    name := name.value + "-js",
	libraryDependencies ++= Seq(
      "es.weso" % "stateparser_sjs0.6_2.11" % "0.1.2"
	, "es.weso" % "srdf-js_sjs0.6_2.11" % "0.0.5"
    ),
	licenses += ("MPL-2.0", url("http://opensource.org/licenses/MPL-2.0"))
  )
  
lazy val turtleparserJVM = turtleparser.jvm
lazy val turtleparserJS = turtleparser.js  
	 
publishMavenStyle := true

bintrayRepository in bintray := "weso-releases"

bintrayOrganization in bintray := Some("weso")

licenses += ("MPL-2.0", url("http://opensource.org/licenses/MPL-2.0"))

resolvers += "Bintray" at "http://dl.bintray.com/weso/weso-releases"

EclipseKeys.useProjectId := true

// Publish site info
site.settings

site.publishSite

site.includeScaladoc()

ghpages.settings

git.remoteRepo := "git@github.com:labra/turtleparser-with-combinators.git"

/* The following line is to download test files from W3c.
   It would be better to do it only when there is internet connection 
   */
resourceGenerators in Test <+= Def.task {
  val location = url("http://www.w3.org/2013/TurtleTests/TESTS.zip")
  IO.unzipURL(location, resourceManaged.value / "downloadedTests").toSeq
}

resolvers += Resolver.bintrayRepo("labra", "maven")
