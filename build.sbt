val dottyVersion = "0.24.0-bin-20200320-30f8c6f-NIGHTLY"

val pubSettings = Seq(
  publishMavenStyle := true,
  bintrayOrganization := Some("blocke"),
  bintrayReleaseOnPublish in ThisBuild := true,
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  bintrayRepository := "releases",
  bintrayPackageLabels := Seq("scala", "dotty", "json")
)

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
        "co.blocke" %% "dotty-reflection" % "class360_3add25", //"0.0.14",
        "munit" %% "munit" % "0.6.z-3" % "test"   // special build of munit compatible with Dotty 0.24
      )
  )
