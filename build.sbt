ThisBuild / organization := "org.kapunga"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.1"

ThisBuild / scalacOptions ++= List(
  "-Ykind-projector",
  "-Xfatal-warnings")

import scala.scalanative.build.*

lazy val whiskerCore = crossProject(JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("whisker-core"))
  .settings(
    name := "Whisker Core",
    libraryDependencies ++= Seq(
      "org.http4s" %%% "http4s-core" % "0.23.23"
    ))

lazy val whiskerClient = (project in file("whisker-client"))
  .enablePlugins(ScalaNativePlugin)
  .settings(
    name := "Whisker Client",
    logLevel := Level.Info,
    libraryDependencies ++= Seq(
      "co.fs2"         %%% "fs2-core"            % "3.10-4b5f50b",
      "co.fs2"         %%% "fs2-io"              % "3.10-4b5f50b",
      "org.http4s"     %%% "http4s-ember-client" % "0.23.23-101-eb5dd80-SNAPSHOT",
      "org.http4s"     %%% "http4s-circe"        % "0.23.23"
    ),
    resolvers += "s01-oss-sonatype-org-snapshots" at "https://s01.oss.sonatype.org/content/repositories/snapshots",
    nativeConfig ~= { c =>
    c.withLTO(LTO.none) // thin
      .withMode(Mode.debug) // releaseFast
      .withGC(GC.immix) // commix
    }
  ).dependsOn(whiskerCore.native)

lazy val whiskerServer = (project in file("whisker-server"))
  .settings(
    name := "Whisker Server",
    logLevel := Level.Info,
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-ember-server" % "0.23.23",
      "org.http4s" %% "http4s-ember-client" % "0.23.23",
      "org.http4s" %% "http4s-dsl"          % "0.23.23",
      "org.http4s" %% "http4s-circe"        % "0.23.23"
    )
  ).dependsOn(whiskerCore.jvm)
