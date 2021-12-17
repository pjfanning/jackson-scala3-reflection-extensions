import sbtghactions.JavaSpec.Distribution.Zulu

name := "jackson-scala3-reflection-extensions"
organization := "com.github.pjfanning"

ThisBuild / version := "2.13.1-SNAPSHOT"

ThisBuild / scalaVersion := "3.0.2"

val jacksonVersion = "2.13.0"
val scala3ReflectionVersion = "1.1.1"
//"1.0.2+4-d3e90186-SNAPSHOT"

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.0+17-d1b516ab-SNAPSHOT",
  //"com.github.pjfanning" %% "scala3-reflection" % scala3ReflectionVersion,
  "co.blocke" %% "scala-reflection" % scala3ReflectionVersion,
  "org.scalatest" %% "scalatest" % "3.2.10" % Test
)

//addCompilerPlugin("com.github.pjfanning" %% "scala3-reflection" % scala3ReflectionVersion)

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
