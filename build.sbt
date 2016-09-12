lazy val commonSettings = Seq(
  version := "0.4.2",
  scalaVersion := "2.11.8",
  organization := "com.unstablebuild",
  organizationName := "unstablebuild.com",
  homepage := Some(url("https://github.com/lucastorri/settler")),
  organizationHomepage := Some(url("http://unstablebuild.com")),
  crossScalaVersions := Seq(scalaVersion.value),
  licenses := Seq("MIT License" -> url("https://opensource.org/licenses/MIT")),
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.0.0" % "test"
  ),
  resolvers ++= Seq(),
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  publishArtifact in Test := false,
  pomIncludeRepository := (_ => false),
  pomExtra :=
    <scm>
      <url>git@github.com:lucastorri/settler.git</url>
      <connection>scm:git:git@github.com:lucastorri/settler.git</connection>
    </scm>
    <developers>
      <developer>
        <id>lucastorri</id>
        <name>Lucas Torri</name>
        <url>http://unstablebuild.com</url>
      </developer>
    </developers>
)

lazy val root = project.in(file("."))
  .settings(commonSettings: _*)
  .dependsOn(macros)
  .settings(
    name := "settler"
  )

lazy val macros = project.in(file("macros"))
  .settings(commonSettings: _*)
  .settings(
    name := "settler-macros",
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.3.0",
      "org.scala-lang" % "scala-reflect" % scalaVersion.value
    )
  )
