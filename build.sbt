name := "dottyjack"
organization in ThisBuild := "co.blocke"
val dottyVersion =  "0.28.0-bin-SNAPSHOT"
val reflectionLibVersion = "c80908_SNAPSHOT"

lazy val root = (project in file("."))
  .settings(settings)
  .settings(publishArtifact := false)
  .settings(publish := {})
  .settings(crossScalaVersions := Nil)
  .aggregate(dottyjack)

lazy val dottyjack = (project in file("core"))
  .settings(settings)
  .settings(
    name := "dottyjack",
    doc := null,  // disable dottydoc for now
    sources in (Compile, doc) := Seq(),
    libraryDependencies ++= commonDependencies,
    Test / parallelExecution := false,
    
    // This messy stuff turns off reflection compiler plugin except for test case code
    addCompilerPlugin("co.blocke" %% "scala-reflection" % reflectionLibVersion),
    autoCompilerPlugins := false,
    ivyConfigurations += Configurations.CompilerPlugin,
    scalacOptions in Test ++= Classpaths.autoPlugins(update.value, Seq(), true)
  )


//==========================
// Dependencies
//==========================
lazy val dependencies =
  new {
    val dottyReflection = "co.blocke"     %% "scala-reflection"  % reflectionLibVersion
    val munit           = "org.scalameta" %% "munit"             % "0.7.12+51-8feb6e8b-SNAPSHOT" % Test
    val commonsCodec    = "commons-codec" % "commons-codec"      % "1.12"
    val json4sCore      = "org.json4s"    % "json4s-core_2.13"   % "3.6.6"
    val json4sNative    = "org.json4s"    % "json4s-native_2.13" % "3.6.6" % Test
  }

lazy val commonDependencies = Seq(
  dependencies.dottyReflection,
  dependencies.commonsCodec,
  dependencies.json4sCore,
  dependencies.json4sNative,
  dependencies.munit
)

//==========================
// Settings
//==========================
lazy val settings = 
  commonSettings ++
  publishSettings

lazy val compilerOptions = Seq(
  "-unchecked",
  "-feature",
  "-language:implicitConversions",
  "-deprecation",
  "-encoding",
  "utf8"
)

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  // resolvers += Resolver.jcenterRepo,
  resolvers += "co.blocke releases buildResolver" at "https://dl.bintray.com/blocke/releases",
  scalaVersion := dottyVersion,
  testFrameworks += new TestFramework("munit.Framework")
)

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  bintrayOrganization := Some("blocke"),
  bintrayReleaseOnPublish in ThisBuild := true,
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  bintrayRepository := "releases",
  bintrayPackageLabels := Seq("scala", "dotty", "reflection")
)

