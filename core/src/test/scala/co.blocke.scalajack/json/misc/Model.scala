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