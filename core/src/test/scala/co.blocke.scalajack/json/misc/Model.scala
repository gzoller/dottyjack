package co.blocke.scalajack
package json.misc

trait Pet { val name: String }
case class Dog[A](name: String, kind: A) extends Pet
