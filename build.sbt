enablePlugins(ScalaJSPlugin)

name := "explorer"
scalaVersion := "3.1.1"

scalaJSUseMainModuleInitializer := true

libraryDependencies ++= Seq(
  "com.raquo" %%% "laminar" % "0.14.2",
  "com.lihaoyi" %%% "upickle" % "1.6.0",
  "be.doeraene" %%% "url-dsl" % "0.4.0",
  "com.softwaremill.sttp.client3" %%% "core" % "3.5.2"
)
