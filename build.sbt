name := "calendar"

version := "0.1"

scalaVersion := "2.12.6"

val akkaHttpVersion = "10.1.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-actor" % "2.5.12",
  "com.typesafe.akka" %% "akka-stream" % "2.5.11",
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,

  "com.google.guava" % "guava" % "12.0",

  //test
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)
