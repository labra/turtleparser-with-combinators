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
	  "org.scala-lang.modules" %%% "scala-parser-combinators" % "1.0.4"
	, "org.scalatest" %%% "scalatest" % "3.0.0-M15" % "test"
	)
  ).
  jvmSettings(
    name := name.value + "-jvm",
	libraryDependencies ++= Seq(
      "es.weso" % "stateparser-jvm_2.11" % "0.1.0"
	, "es.weso" % "SRDF-jvm_2.11" % "0.0.5"
    ),
	licenses += ("MPL-2.0", url("http://opensource.org/licenses/MPL-2.0"))
  ).
  jsSettings(
    name := name.value + "-js",
	libraryDependencies ++= Seq(
      "es.weso" % "stateparser-js_2.11" % "0.1.0"
	, "es.weso" % "SRDF-js_2.11" % "0.0.5"
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

