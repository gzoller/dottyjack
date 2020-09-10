name := "dottyjack"
organization in ThisBuild := "co.blocke"
val dottyVersion =  "0.27.0-RC1"
val reflectionLibVersion = "0.2.1" //"streamliner_75ecfc"

// lazy val root = (project in file("."))
//   .settings(settings)
//   .settings(publishArtifact := false)
//   .settings(publish := {})
//   .settings(crossScalaVersions := Nil)
//   .aggregate(dottyjack)

// lazy val dottyjack = (project in file("core"))
lazy val root = (project in file("."))
  .settings(settings)
  .settings(
    name := "dottyjack",
    doc := null,  // disable dottydoc for now
    sources in (Compile, doc) := Seq(),
    libraryDependencies ++= commonDependencies,
    Test / parallelExecution := false,
    
    // This messy stuff turns off reflection compiler plugin except for test case code
    autoCompilerPlugins := false,
    ivyConfigurations += Configurations.CompilerPlugin,
    scalacOptions in Test ++= Classpaths.autoPlugins(update.value, Seq(), true)
  )

addCompilerPlugin("co.blocke" %% "dotty-reflection" % reflectionLibVersion)

//==========================
// Dependencies
//==========================
lazy val dependencies =
  new {
    val dottyReflection = "co.blocke" %% "dotty-reflection" % reflectionLibVersion
    val munit = "org.scalameta" %% "munit" % "0.7.12" % Test
    val commonsCodec    = "commons-codec" % "commons-codec" % "1.12"
    // val kyro            = "com.esotericsoftware" % "kryo" % "5.0.0-RC9" % Test
    val json4sCore      = "org.json4s" % "json4s-core_2.13" % "3.6.6" % Test
    val json4sNative    = "org.json4s" % "json4s-native_2.13" % "3.6.6" % Test
  }

lazy val commonDependencies = Seq(
  dependencies.dottyReflection,
  dependencies.commonsCodec,
  dependencies.json4sCore,
  dependencies.json4sNative,
  // dependencies.kyro,
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



/*
val dottyVersion = "0.26.0-RC1"
val reflectionLibVersion = "paths2_b38af6"

// lazy val basicSettings = Seq(
//   organization := "co.blocke",
//   startYear := Some(2015),
//   publishArtifact in (Compile, packageDoc) := false, // disable scaladoc due to bug handling annotations
//   scalaVersion := dottyVersion,
//   addCompilerPlugin("co.blocke" %% "dotty-reflection" % "paths2_b38af6"),
//   // resolvers += Resolver.jcenterRepo,  <-- Use this one once we're GA and co-publishing to JCenter!
//   resolvers += "co.blocke releases buildResolver" at "https://dl.bintray.com/blocke/releases",
//   // coverageMinimum := 98, 
//   // coverageFailOnMinimum := true,
//   doc := null,  // disable dottydoc for now
//   sources in (Compile, doc) := Seq(),
//   Test / parallelExecution in ThisBuild := false,
//   scalacOptions ++= Seq(
//     "-feature",
//     "-deprecation",
//     "-encoding",
//     "UTF8",
//     "-unchecked"
//   ),
//   javacOptions += "-g",
//   scalacOptions in Test ++= Seq(
//     "-language:implicitConversions"
//   ),
//   testFrameworks += new TestFramework("munit.Framework"),
//   testOptions in Test += Tests.Argument("-oDF")
// )

lazy val root = (project in file("."))
  .settings(settings)
  .settings(publishArtifact := false)
  .settings(publish := {})
  .settings(crossScalaVersions := Nil)
  .aggregate(dottyjack)  //, scalajack_benchmarks)

lazy val dottyjack = project
  .in(file("core"))
  .settings(settings)
  .settings(Seq(addCompilerPlugin("co.blocke" %% "dotty-reflection" % reflectionLibVersion)))

  // lazy val commonSettings = Seq(
  //   organization := "co.blocke",
  //   startYear := Some(2015),
  //   publishArtifact in (Compile, packageDoc) := false, // disable scaladoc due to bug handling annotations
  //   scalaVersion := dottyVersion,
  //   addCompilerPlugin("co.blocke" %% "dotty-reflection" % "paths2_b38af6"),
  //   // resolvers += Resolver.jcenterRepo,  <-- Use this one once we're GA and co-publishing to JCenter!
  //   resolvers += "co.blocke releases buildResolver" at "https://dl.bintray.com/blocke/releases",
  //   // coverageMinimum := 98, 
  //   // coverageFailOnMinimum := true,
  //   doc := null,  // disable dottydoc for now
  //   sources in (Compile, doc) := Seq(),
  //   Test / parallelExecution in ThisBuild := false,
  //   scalacOptions ++= Seq(
  //     "-feature",
  //     "-deprecation",
  //     "-encoding",
  //     "UTF8",
  //     "-unchecked"
  //   ),
  //   javacOptions += "-g",
  //   scalacOptions in Test ++= Seq(
  //     "-language:implicitConversions"
  //   ),
  //   testFrameworks += new TestFramework("munit.Framework"),
  //   testOptions in Test += Tests.Argument("-oDF")
  // )

  //==========================
  // Dependencies
  //==========================
  lazy val dependencies =
    new {
      val commonsCodec    = "commons-codec" % "commons-codec" % "1.12"
      val dottyReflection = "co.blocke" %% "dotty-reflection" % reflectionLibVersion
      val munit           = "org.scalameta" %% "munit" % "0.7.11" % Test
      val json4sCore      = "org.json4s" % "json4s-core_2.13" % "3.6.6" % Test
      val json4sNative    = "org.json4s" % "json4s-native_2.13" % "3.6.6" % Test
    }

  lazy val commonDependencies = Seq(
    dependencies.commonsCodec,
    dependencies.dottyReflection,
    dependencies.munit,
    dependencies.json4sCore,
    dependencies.json4sNative
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
    organization := "co.blocke",
    startYear := Some(2015),
    publishArtifact in (Compile, packageDoc) := false, // disable scaladoc due to bug handling annotations
    scalacOptions ++= compilerOptions,
    // resolvers += Resolver.jcenterRepo,
    scalaVersion := dottyVersion,
    doc := null,  // disable dottydoc for now
    sources in (Compile, doc) := Seq(),
    Test / parallelExecution in ThisBuild := false,
    libraryDependencies ++= commonDependencies,
    testFrameworks += new TestFramework("munit.Framework")
  )

  lazy val publishSettings = Seq(
    publishMavenStyle := true,
    bintrayOrganization := Some("blocke"),
    bintrayReleaseOnPublish in ThisBuild := true,
    licenses += ("MIT", url("https://opensource.org/licenses/MIT")),
    bintrayRepository := "releases",
    bintrayPackageLabels := Seq("scala", "dotty", "json")
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
*/