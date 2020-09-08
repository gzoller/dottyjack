## dottyjack

DottyJack is an experimental implementation of a JSON parser/serializer, derived from ScalaJack, for the Dotty language.  Its purpose is to see how closely the functionality of ScalaJack can be carried over onto the Dotty language platform.  Unlike ScalaJack, only JSON wire format will be supported at this time.  Once Dotty becomes Scala 3, DottyJack will migrate to ScalaJack 7, and remaining features and wire formats will be supported.

Usage is virtually identical to ScalaJack, so please refer to those docs.

In your sbt file in settings you'll need to include:  
```
   resolvers += "co.blocke releases resolver" at "https://dl.bintray.com/blocke/releases"
```

```scala
import co.blocke.dottyjack._

case class Person(name: String, age: Int)

val dj = DottyJack()
val js = dj.render(Person("Mike",34))  // js == """{"name":"Mike","age":34}"""
val inst = dj.read[Person](js) // re-constitutes original Person
```


### Notes:

* 0.0.1 Initial feature release
* 0.0.2 Full JSON feature support + consume macro-based reflection (dotty-reflection)
* 0.1.1 Build against Dotty 0.27.0-RC1 and JDK13.  Support compiler plugin performance boost


### A word about performance...
Compared to pre-Dotty ScalaJack, which used Scala 2.x runtime reflection, DottyJack is both much faster, and much slower than before.  For classes
that can be reflected on at compile-time (anytime you use RType.of[...]) there's a significant performance boost.  For any time the 
library must fall back to runtime reflection (inspection in Dotty-speak), RType.of(...) or RType.inTermsOf[](), performance becomes alarmingly poor.  The 
reason is that unlike Scala 2.x, which held a lot of reflection information ready-to-go in the compiled class file, Dotty must parse the .tasty file by 
first reading it (file IO!).  For a comparison: a macro-readable class (reflection) might process in 2 or 3 milliseconds.  A class that needs Dotty 
inspection (runtime) might be more than 1 or 2 full seconds to process.  YIKES!  

There is an optional compiler plugin that mitigates this problem.  If you use it your performance for DottyJack trait handling will go way up.  To use, add
the following to your build.sbt:

```scala
addCompilerPlugin("co.blocke" %% "dotty-reflection" % VERSION)
```
where VERSION is the latest dotty-reflection version (found by looking at the Download badge here: www.blocke.co/dotty-reflection)
