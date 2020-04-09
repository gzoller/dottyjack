package co.blocke.scalajack
package json.structures


// === Eithers
case class Parrot(color: String)
case class DumpTruck(axles: Int)
case class EitherHolder[L, R](either: Either[L, R])

case class Chair(numLegs: Int)
case class Table(numLegs: Int)

trait Pet { val name: String }
case class Dog[A](name: String, kind: A) extends Pet