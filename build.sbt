val dottyVersion = "0.24.0-bin-20200320-30f8c6f-NIGHTLY"

val pubSettings = Seq(
  publishMavenStyle := true,
  bintrayOrganization := Some("blocke"),
  bintrayReleaseOnPublish in ThisBuild := true,
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  bintrayRepository := "releases",
  bintrayPackageLabels := Seq("scala", "dotty", "json")
)

resolvers += "co.blocke provisional resolver" at "https://dl.bintray.com/blocke/provisional"

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
