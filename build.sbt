import sbtghactions.JavaSpec.Distribution.Zulu

name := "jackson-scala3-reflection-extensions"
organization := "com.github.pjfanning"
description := "Jackson scala3 support that uses scala3-reflection to get type info"

ThisBuild / scalaVersion := "3.3.1"

val jacksonVersion = "2.16.0"
val scala3ReflectionVersion = "1.3.1"

resolvers ++= Resolver.sonatypeOssRepos("snapshots")

libraryDependencies ++= Seq(
  "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion,
  "com.github.pjfanning" %% "scala3-reflection" % scala3ReflectionVersion,
  "org.scala-lang" %% "scala3-staging" % scalaVersion.value,
  "org.slf4j" % "slf4j-api" % "2.0.9",
  "org.scalatest" %% "scalatest" % "3.2.17" % Test,
  "org.slf4j" % "slf4j-simple" % "2.0.9" % Test
)

addCompilerPlugin("com.github.pjfanning" %% "scala3-reflection" % scala3ReflectionVersion)

homepage := Some(url("https://github.com/pjfanning/jackson-scala3-reflection-extensions"))

licenses := Seq("APL2" -> url("https://www.apache.org/licenses/LICENSE-2.0.txt"))

developers := List(
  Developer(id="pjfanning", name="PJ Fanning", email="", url=url("https://github.com/pjfanning"))
)

// build.properties
Compile / resourceGenerators += Def.task {
  val file = (Compile / resourceManaged).value / "com" / "github" / "pjfanning" / "jackson" / "reflection" / "build.properties"
  val contents = "version=%s\ngroupId=%s\nartifactId=%s\n".format(version.value, organization.value, name.value)
  IO.write(file, contents)
  Seq(file)
}.taskValue

Test / parallelExecution := false

ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec(Zulu, "8"))
ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowBuild := Seq(WorkflowStep.Sbt(List("test")))
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
