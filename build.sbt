val dottyVersion = "0.26.0-RC1"

lazy val basicSettings = Seq(
  organization := "co.blocke",
  startYear := Some(2015),
  publishArtifact in (Compile, packageDoc) := false, // disable scaladoc due to bug handling annotations
  scalaVersion := dottyVersion,
  // resolvers += Resolver.jcenterRepo,  <-- Use this one once we're GA and co-publishing to JCenter!
  resolvers += "co.blocke releases buildResolver" at "https://dl.bintray.com/blocke/releases",
  // coverageMinimum := 98, 
  // coverageFailOnMinimum := true,
  doc := null,  // disable dottydoc for now
  sources in (Compile, doc) := Seq(),
  Test / parallelExecution in ThisBuild := false,
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-encoding",
    "UTF8",
    "-unchecked"
  ),
  javacOptions += "-g",
  scalacOptions in Test ++= Seq(
    "-language:implicitConversions"
  ),
  testFrameworks += new TestFramework("munit.Framework"),
  testOptions in Test += Tests.Argument("-oDF")
)

val pubSettings = Seq(
  publishMavenStyle := true,
  bintrayOrganization := Some("blocke"),
  bintrayReleaseOnPublish in ThisBuild := true,
  licenses += ("MIT", url("https://opensource.org/licenses/MIT")),
  bintrayRepository := "releases",
  bintrayPackageLabels := Seq("scala", "dotty", "json")
)

lazy val root = (project in file("."))
  .settings(basicSettings: _*)
  .settings(publishArtifact := false)
  .settings(publish := {})
  .settings(crossScalaVersions := Nil)
  .aggregate(dottyjack)  //, scalajack_benchmarks)

lazy val dottyjack = project
  .in(file("core"))
  // .enablePlugins(JmhPlugin)
  .settings(basicSettings)
  .settings(pubSettings: _*)
  .settings(
    libraryDependencies ++= 
      Seq(
        "commons-codec" % "commons-codec" % "1.12",
        "co.blocke" %% "dotty-reflection" % "plugin_921e14",
        "org.scalameta" %% "munit" % "0.7.11" % Test,
        "org.json4s" % "json4s-core_2.13" % "3.6.6" % Test,
        "org.json4s" % "json4s-native_2.13" % "3.6.6" % Test
      )
  )

  /*
  lazy val scalajack_benchmarks = project
    .in(file("benchmarks"))
    .enablePlugins(JmhPlugin)
    .settings(publishArtifact := false)
    .settings(publish := {})
    .settings(basicSettings: _*)
    .settings(
      // mainClass in (Jmh, run) := Some("bench.BaseBenchmarks"), // custom main for jmh:run
      // javaOptions += "-DBENCH_COMPILER_CLASS_PATH=" + Attributed.data((fullClasspath in (`dotty-bootstrapped`, Compile)).value).mkString("", File.pathSeparator, ""),
      // javaOptions += "-DBENCH_CLASS_PATH=" + Attributed.data((fullClasspath in (`dotty-library-bootstrapped`, Compile)).value).mkString("", File.pathSeparator, ""),
      libraryDependencies += "ch.epfl.lamp" %% "dotty-compiler" % dottyVersion
    )
    .dependsOn(scalajack)
    */
