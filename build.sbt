import Dependencies.Libraries
import sbt.url

ThisBuild / scalaVersion := "3.3.4"
ThisBuild / organization := "org.kapunga"
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision
ThisBuild / coverageEnabled := true
ThisBuild / licenses := List("MIT" -> url("https://opensource.org/license/mit"))
ThisBuild / developers := List(
  Developer(
    id = "kapunga",
    name = "Paul (Thor) Thordarson",
    email = "kapunga@gmail.com",
    url = url("https://github.com/kapunga")
  )
)

lazy val core = (project in file("core"))
  .settings(
    publish / skip := true,
    name := "whisker-core",
    description := "Core components of Whisker.",
    startYear := Some(2025),
    libraryDependencies ++= Libraries.terminus
  )
