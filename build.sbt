val dottyVersion = "0.24.0-bin-20200320-30f8c6f-NIGHTLY"

lazy val basicSettings = Seq(
  organization := "co.blocke",
  startYear := Some(2015),
  publishArtifact in (Compile, packageDoc) := false, // disable scaladoc due to bug handling annotations
  scalaVersion := dottyVersion,
  resolvers += Resolver.jcenterRepo,
  resolvers += "co.blocke provisional resolver" at "https://dl.bintray.com/blocke/provisional",
    // coverageMinimum := 98, // really this should be 96% but mongo isn't quite up to that yet
  // coverageFailOnMinimum := true,
  Test / parallelExecution in ThisBuild := false,
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-encoding",
    "UTF8",
    "-unchecked"
  ),
  testFrameworks += new TestFramework("munit.Framework"),
  testOptions in Test += Tests.Argument("-oDF")
)

val pubSettings = Seq(
  publishMavenStyle := true,
  bintrayOrganization := Some("blocke"),
  bintrayReleaseOnPublish in ThisBuild := true,
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  bintrayRepository := "releases",
  bintrayPackageLabels := Seq("scala", "dotty", "json")
)

lazy val root = (project in file("."))
  .settings(basicSettings: _*)
  .settings(publishArtifact := false)
  .settings(publish := {})
  .settings(crossScalaVersions := Nil)
  .aggregate(scalajack)//, scalajack_benchmarks)

lazy val scalajack = project
  .in(file("core"))
  .settings(basicSettings)
  .settings(pubSettings: _*)
  .settings(
    libraryDependencies ++= 
      Seq(
        "commons-codec" % "commons-codec" % "1.12",
        "co.blocke" %% "dotty-reflection" % "38a084_SNAPSHOT",
        // "org.json4s" %% "json4s-core" % "3.6.6" % "test",
        "munit" %% "munit" % "0.6.z-3" % "test"   // special build of munit compatible with Dotty 0.24
      )
  )

  /*
lazy val root = project
  .in(file("."))
  .settings(pubSettings: _*)
  .settings(
    name := "dottyjack",

    resolvers += Resolver.jcenterRepo,

    scalaVersion := dottyVersion,

    Test / parallelExecution := false,
    
    scalacOptions ++= Seq("-language:implicitConversions"),

    testFrameworks += new TestFramework("munit.Framework"),

    libraryDependencies ++= 
      Seq(
        "commons-codec" % "commons-codec" % "1.12",
        "co.blocke" %% "dotty-reflection" % "4269bf_SNAPSHOT",
        // "org.json4s" %% "json4s-core" % "3.6.6" % "test",
        "munit" %% "munit" % "0.6.z-3" % "test"   // special build of munit compatible with Dotty 0.24
      )
  )
  */
