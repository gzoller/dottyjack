package co.blocke.scalajack
package json.misc

import co.blocke.dottyjack._

// === Default Values
@Collection(name = "myDefaults")
case class DefaultOpt(
    @DBKey(index = 1) name:String,
    age:              Option[Int] = Some(19))

case class HasDefaults(
    name: String,
    age:  Option[Int],
    pet:  Pet         = Dog("Fido", true))
case class SimpleHasDefaults(name: String, age: Int = 5)


// === Change field name
case class MapFactor(
  @Change(name = "foo_bar") fooBar:String,
  @Change(name = "a_b") thingy:   Long,
  count:                          Int)
class MapFactorPlain(@Change(name = "pilot") val driver: String) {
  @Change(name = "foo_bar") var fooBar: String = ""
  @Change(name = "a_b") var thingy: Long = 0L
  var count: Int = 0
}

// === View/Splice
object Num extends Enumeration {
  val A, B, C = Value
}

case class Master(
    name:     String,
    stuff:    List[String],
    more:     List[Encapsulated],
    nest:     Encapsulated,
    maybe:    Option[String],
    mymap:    Map[String, Int],
    flipflop: Boolean,
    big:      Long,
    num:      Num.Value,
    age:      Int) {
  val foo: String = "yikes!"
}
case class Encapsulated(foo: String, bar: Boolean)
case class View1(name: String, big: Long, maybe: Option[String])
case class View2(name: String, flipflop: Boolean, mymap: Map[String, Int])

case class Partial(name: String, bogus: Int)
case class Empty(
    name:  String,
    stuff: List[String] // should be empty in test
)

case class NoMatch(bogus: Boolean, nah: Int)

// === Complex Relationships
trait Parent[A, B] { val a: A; val b: B }
case class Child[A, B, C](a: A, b: B, c: C) extends Parent[A, B]

object Kind extends Enumeration {
  val Lab, Pug = Value
}
trait Pet { val name: String }
case class Dog[A](name: String, kind: A) extends Pet
case class PetHolder[T <: Pet](payload: T) {
  type kind = T
}