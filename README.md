## dottyjack

DottyJack is an experimental implementation of a JSON parser/serializer, derived from ScalaJack, for the Dotty language.  Its purpose is to see how closely the functionality of ScalaJack can be carried over onto the Dotty language platform.  Unlike ScalaJack, only JSON wire format will be supported at this time.  Once Dotty becomes Scala 3, DottyJack will migrate to ScalaJack 7, and remaining features and wire formats will be supported.

Usage is virtually identical to ScalaJack, so please refer to those docs.

In your sbt file in settings you'll need to include:  ```  resolvers += "co.blocke releases resolver" at "https://dl.bintray.com/blocke/releases"```

```scala
import co.blocke.dottyjack._

case class Person(name: String, age: Int)

val dj = DottyJack()
val js = dj.render(Person("Mike",34))  // js == """{"name":"Mike","age":34}"""
val inst = dj.render[Person](js) // re-constitutes original Person
```


### Notes:

* 0.0.1 Initial feature release


### A word about performance...
There are deep structural changes in Dotty vs Scala 2.x, one of which was the choice to remove the Scala runtime reflection.  In its place is a tasty file, much like an extended class file, which has all the juicy bits one needs for reflection.  In Scala 2.x reflected information was encoded into case classes and was available at runtime.  In Dotty, the tasty file must be read.  There's a clear and significant performance difference between memory access and file IO.  So the bad news--DottyJack's performance for first-seen classes will be very poor vs old ScalaJack.  The good news--all of ScalaJack's deep reflective abilities are available in DottyJack plus support for Dotty's new features (e.g. union types).

While initially this sounds like disappointing news, for many use cases it may not be that impactful.  Like ScalaJack, DottyJack caches classes it has seen, so once cached it runs super-fast like ScalaJack does.  So once "primed", DottyJack is very performant.  Many use cases require serialization of the same set of classes over and over again, so a one-time hit of reading the tasty file will be quickly amortized over the rest of your serializations.

There is also a possibility of incorporating compile-time macros into DottyJack, which would move 80% of the tasty file overhead into the compilation process (except for runtime dynamic trait resolution, i.e. traits with a type hint).  This will have a very positive performance benefit.  The priniciple limitation on this approach is my own learning curve!  Stay tuned...
