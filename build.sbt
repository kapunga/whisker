ThisBuild / organization := "org.kapunga"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.1"

import scala.scalanative.build.*

lazy val whiskerCore = crossProject(JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("whisker-core"))
  .settings(name := "Whisker Core")

lazy val whiskerClient = (project in file("whisker-client"))
  .enablePlugins(ScalaNativePlugin)
  .settings(
    name := "Whisker Client",
    logLevel := Level.Info,
    libraryDependencies ++= Seq(
      "com.armanbilge" %%% "epollcat"            % "0.1.5",
      "org.http4s"     %%% "http4s-ember-client" % "0.23.23",
      "org.http4s"     %%% "http4s-circe"        % "0.23.23"
    ),
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
