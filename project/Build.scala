import sbt._
import Keys._
import Tests._
import scoverage.ScoverageSbtPlugin

object Ostrich extends Build {
  val branch = Process("git" :: "rev-parse" :: "--abbrev-ref" :: "HEAD" :: Nil).!!.trim
  val suffix = if (branch == "master") "" else "-SNAPSHOT"

  val libVersion = "9.18.0" + suffix
  val utilVersion = "6.34.0" + suffix

  val sharedSettings = Seq(
    name := "ostrich",
    version := libVersion,
    organization := "com.twitter",
    scalaVersion := "2.11.8",
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    javacOptions in doc := Seq("-source", "1.8"),
    parallelExecution in Test := false,
    resolvers += "twitter repo" at "https://maven.twttr.com",
    libraryDependencies ++= Seq(
      "com.twitter" %% "util-core" % utilVersion,
      "com.twitter" %% "util-eval" % utilVersion,
      "com.twitter" %% "util-logging" % utilVersion,
      "com.twitter" %% "util-jvm" % utilVersion,
      "com.twitter" %% "scala-json" % "3.0.2"
    ),

    libraryDependencies ++= Seq(
      "junit" % "junit" % "4.10" % "test",
      "org.mockito" % "mockito-all" % "1.9.5" % "test",
      "org.scalatest" %% "scalatest" % "2.2.4" % "test"
    ),

    ScoverageSbtPlugin.ScoverageKeys.coverageHighlighting := true,

    publishMavenStyle := true,
    publishTo <<= version { (v: String) =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT"))
        Some("sonatype-snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("sonatype-releases"  at nexus + "service/local/staging/deploy/maven2")
    },

    publishArtifact in Test := false,

    pomIncludeRepository := { x => false },

    pomExtra := (
      <url>https://github.com/twitter/ostrich</url>
      <licenses>
        <license>
          <name>Apache 2</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
          <distribution>repo</distribution>
          <comments>A business-friendly OSS license</comments>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:twitter/ostrich.git</url>
        <connection>scm:git:git@github.com:twitter/ostrich.git</connection>
      </scm>
      <developers>
        <developer>
          <id>twitter</id>
          <name>Twitter Inc.</name>
          <url>https://www.twitter.com/</url>
        </developer>
      </developers>
    )
  )

  lazy val ostrich = Project(
    id = "ostrich",
    base = file(".")
  ).settings(sharedSettings)
}
