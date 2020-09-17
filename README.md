
## dottyjack

DottyJack is an experimental implementation of a JSON parser/serializer, derived from ScalaJack, for the Dotty language.  Its purpose is to see how closely the functionality of ScalaJack can be carried over onto the Dotty language platform.  Unlike ScalaJack, only JSON wire format will be supported at this time.  Once Dotty becomes Scala 3, DottyJack will migrate to ScalaJack 7, and remaining features and wire formats will be supported.

Usage is virtually identical to ScalaJack, so please refer to those docs.

In your sbt file in settings you'll need to include:  
```
   resolvers += "co.blocke releases resolver" at "https://dl.bintray.com/blocke/releases"
```
To use the (highly-recommended) reflection compiler plug-in, add to build.sbt:
```
addCompilerPlugin("co.blocke" %% "dotty-reflection" % VERSION)
```
where VERSION is the latest dotty-reflection version found by looking at the Download badge here: [www.blocke.co/dotty-reflection](http://www.blocke.co/dotty-reflection)

To use:
```scala
import co.blocke.dottyjack._

case class Person(name: String, age: Int)

val dj = DottyJack()
val js = dj.render(Person("Mike",34))  // js == """{"name":"Mike","age":34}"""
val inst = dj.read[Person](js) // re-constitutes original Person
```


### Notes:
* 0.1.1 Consume latest/fastest dotty-reflection.  Clean up RType to get rid of Transporter wrapper
* 0.1.0 Build against Dotty 0.27.0-RC1 and JDK13.  Support compiler plugin performance boost
* 0.0.2 Full JSON feature support + consume macro-based reflection (dotty-reflection)
* 0.0.1 Initial feature release


### A word about performance...
Compared to pre-Dotty ScalaJack, which used Scala 2.x runtime reflection, DottyJack is up to 30% faster in many cases when used with the highly-recommended dotty-reflection compiler plugin.  

