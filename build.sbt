import sbtghactions.JavaSpec.Distribution.Zulu

name := "jackson-scala3-reflection-extensions"
organization := "com.github.pjfanning"

ThisBuild / version := "2.13.1-SNAPSHOT"

ThisBuild / scalaVersion := "3.0.2"

val jacksonVersion = "2.13.0"
val scalaReflectionVersion = "1.1.1"

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.1",
  //"com.github.pjfanning" %% "scala3-reflection" % scalaReflectionVersion,
  "co.blocke" %% "scala-reflection" % scalaReflectionVersion,
  "org.scalatest" %% "scalatest" % "3.2.10" % Test
)

//addCompilerPlugin("co.blocke" %% "scala-reflection" % scalaReflectionVersion)

ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec(Zulu, "8"))
ThisBuild / githubWorkflowBuild := Seq(WorkflowStep.Sbt(List("test")))
ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches := Seq(
  RefPredicate.Equals(Ref.Branch("main")),
  RefPredicate.StartsWith(Ref.Tag("v"))
)

ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    List("ci-release"),
    env = Map(
      "PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET" -> "${{ secrets.PGP_SECRET }}",
      "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}",
      "CI_SNAPSHOT_RELEASE" -> "+publishSigned"
    )
  )
)
