name := "jackson-scala-reflection-extensions"

version := "2.13.0-SNAPSHOT"

scalaVersion := "3.0.2"

val jacksonVersion = "2.13.0"

libraryDependencies ++= Seq(
  "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion,
  "co.blocke" %% "scala-reflection" % "1.0.0",
  "org.scalatest" %% "scalatest" % "3.2.10" % Test
)