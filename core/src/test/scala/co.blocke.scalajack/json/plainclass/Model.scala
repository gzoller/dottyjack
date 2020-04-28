package co.blocke.scalajack
package json.plainclass

import co.blocke.dottyjack.SJCapture
import co.blocke.dotty_reflection._
import co.blocke.dottyjack._

import scala.util._

class InheritSimpleBase(
    @DBKey(index = 50)@Change(name = "bogus") val one:String= "blather"
) {
  // Public data member
  @DBKey(index = 1) @Change(name = "foobar") var two: Int = 5
  @Optional var three: Boolean = true

  // Private var or val
  val notOne: Int = 2

  @Ignore var dontseeme: Int = 90

  // Scala-style getter/setter
  private var _four: Double = 0.1
  @DBKey(index = 2) @Optional def four: Double = _four
  @Change(name = "quatro") def four_=(a: Double): Unit = _four = a

  private var _dontForget: Int = 9
  def dontForget: Int = _dontForget
  def dontForget_=(a: Int): Unit = _dontForget = a

  private var _unused: Double = 0.1
  @Ignore def unused: Double = _unused
  def unused_=(a: Double): Unit = _unused = a
}

class InheritSimpleChild(
    val extra:                                  String,
    @DBKey @Change(name = "uno") override val one:String)
  extends InheritSimpleBase(one) {
  @DBKey(index = 99) var foo: Int = 39
  @Ignore var bogus: String = ""

  private var _nada: Double = 0.1
  def nada: Double = _nada
  @Ignore def nada_=(a: Double): Unit = _nada = a
}

// ---

class ParamBase[T](val thing: T) {
  var item: T = null.asInstanceOf[T]

  private var _cosa: T = null.asInstanceOf[T]
  def cosa: T = _cosa
  def cosa_=(a: T): Unit = _cosa = a
}

class ParamChild[T](override val thing: T) extends ParamBase[T](thing)

// ---

trait TraitBase {
  val thing: Int
  val other: Int
}

class Flower(val thing: Int, val other: Int) extends TraitBase

class WrapTrait[T <: TraitBase](val rose: T) {
  type flower = T
}

// ---

class Fail4(val a: Int, b: Int)

// --

class OptConst(val a: Option[Int]) {
  var b: Option[Int] = Some(3)
}

class UnneededType[T](val a: Int) {
  type item = T
}

//------------------------------------------------------
case class VCDouble(vc: Double) extends AnyVal
class PlayerMix(val name: String, val maybe: Option[Int], val age: Option[VCDouble]) {
  def someConfusingThing() = true

  @Ignore var bogus: String = ""

  // private var _age: VCDouble = VCDouble(0.0)
  // @Optional def age: VCDouble = _age // getter/setter member
  // def age_=(a: VCDouble): Unit = _age = a
}

class BigPlayer(
    override val name: String, 
    override val maybe: Option[Int], 
    override val age: Option[VCDouble]) extends PlayerMix(name,maybe,age) 
  {
  var more: Int = 0
}

class NotAllVals(val a: Int, b: Int, val c: Int)

class Embed(val stuff: List[String], val num: Int)
class Boom(val name: String, val other: Try[Embed])

class Cap(val name: String) extends SJCapture

case class CaseCap(name: String) extends SJCapture