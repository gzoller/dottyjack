resolvers += Resolver.url(
  "co.blocke releases resolver",
  url("https://dl.bintray.com/blocke/releases/")
)(Resolver.ivyStylePatterns)

addSbtPlugin("ch.epfl.lamp" % "sbt-dotty" % "0.4.0")
addSbtPlugin("co.blocke" % "gitflow-packager" % "0.1.9")
addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.6")
addSbtPlugin("pl.project13.scala" % "sbt-jmh" % "0.3.2")