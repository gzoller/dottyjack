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


// === Structures
trait Body
case class FancyBody(message: String) extends Body
case class DefaultBody(message: String = "Unknown body") extends Body
case class AnyBody(stuff: Any) extends Body

trait Hobby
case class InsideHobby(desc: String) extends Hobby

case class Envelope[T <: Body](id: String, body: T) {
  type Giraffe = T
}

// Type member X should be ignored!  Only used internally
case class BigEnvelope[T <: Body, H <: Hobby, X](
    id:    String,
    body:  T,
    hobby: H) {
  type Giraffe = T
  type Hippo = H
  type IgnoreMe = X

  val x: IgnoreMe = null.asInstanceOf[IgnoreMe]
}

case class Bigger(foo: Int, env: Envelope[FancyBody])
