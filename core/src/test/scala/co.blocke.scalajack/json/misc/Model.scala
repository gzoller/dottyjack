package co.blocke.scalajack
package json.misc

import co.blocke.dottyjack._

trait Pet { val name: String }
case class Dog[A](name: String, kind: A) extends Pet

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