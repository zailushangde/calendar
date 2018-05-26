name := "calendar"

version := "0.1"

scalaVersion := "2.12.2"

val akkaHttpVersion = "10.1.1"

val backendDependencies = Seq(
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-actor" % "2.5.12",
  "com.typesafe.akka" %% "akka-stream" % "2.5.11",
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,

  "com.google.guava" % "guava" % "12.0",

  // config loading
  "com.github.pureconfig" %% "pureconfig" % "0.9.1",

  // test
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)

val javascriptDependencies = Seq(
  "org.webjars" % "jquery" % "2.2.1" / "jquery.js" minified "jquery.min.js"
)

lazy val backend = project
  .settings(
    name := "backend",
    description := "the backend for the calendar app",
    libraryDependencies ++= backendDependencies)

lazy val frontend = project
  .settings(
    name := "frontend",
    description := "the frontend for the calendar app",
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.5",
      "org.querki" %%% "jquery-facade" % "1.2",

      // http client
      "fr.hmil" %%% "roshttp" % "2.1.0",

      // json
        "io.circe" %%% "circe-core" % "0.9.3",
        "io.circe" %%% "circe-generic" % "0.9.3",
        "io.circe" %%% "circe-parser" % "0.9.3",
        "io.circe" %%% "circe-generic-extras" % "0.9.3",

      // scala tags
      "com.lihaoyi" %%% "scalatags" % "0.6.2"
    ))
  .settings(
    mainClass := Some("kang.net.Bootstrap"),
    skip in packageJSDependencies := false,
    jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv())
  .settings(
    jsDependencies ++= javascriptDependencies)
  .enablePlugins(ScalaJSPlugin)