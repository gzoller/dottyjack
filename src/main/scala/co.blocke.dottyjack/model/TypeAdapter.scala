package co.blocke.dottyjack
package model

import scala.collection.mutable
import scala.reflect.ClassTag
import co.blocke.dotty_reflection._
import co.blocke.dotty_reflection.infos._
import co.blocke.dotty_reflection.impl.Clazzes._


/**
 * TypeAdapter includes two matching patterns you can use when you extend trait TypeAdapter for your
 * custom adapters.  The two matching behaviors are '===' and '=:='.
 *
 * This difference is because =:= matches children.  Consider:
 *
 *    type Phone = String
 *    case class( name:String, phone:Phone )
 *
 * With =:= both name and phone (String and Phone) will match a TypeAdapter derived from =:=.
 * This is actually what you want if you haven't overridden Phone with its own TypeAdapter... it should default
 * to the TypeAdapter of its base type.
 *
 * But... if you did provide an override PhoneTypeAdapter you want the matching to be strict, so we use  ===
 * in this case.  With strict matching String != Phone.
 *
 */
 /*
object TypeAdapter {

 /*
  abstract class ===[X](implicit ttFactory: TypeTag[X])
    extends TypeAdapterFactory.===[X]
    with ScalarTypeAdapter[X] {
    val scalarType = ttFactory.tpe
    override def create(next: TypeAdapterFactory)(
        implicit
        tt: TypeTag[X]
    ): TypeAdapter[X] = this
  }
  */

  // abstract class =:=[X]
  //   extends TypeAdapterFactory {
  //   self: TypeAdapter[X] =>
  //   def matches(tpe: TypeStructure): Boolean = myTpe.className == tpe.className
  //   def makeTypeAdapter(tpe: TypeStructure): TypeAdapter[X] = self
  // }

}
*/

trait TypeAdapter[T] {
  self =>

  val info: ConcreteType
  def resolved: TypeAdapter[T] = this // Might be something else during Lazy construction
  
  def read(parser: Parser): T
  def write[WIRE](
      t:      T,
      writer: Writer[WIRE],
      out:    mutable.Builder[WIRE, WIRE]): Unit

  def as[U <: TypeAdapter[_]: ClassTag]: U = {
    val runtimeClass = implicitly[ClassTag[U]].runtimeClass
    try {
      runtimeClass.cast(self).asInstanceOf[U]
    } catch {
      case _: ClassCastException =>
        throw new RuntimeException(
          s"$self is not an instance of ${implicitly[ClassTag[U]].runtimeClass}"
        )
    }
  }
}

trait ScalarTypeAdapter[T] extends TypeAdapter[T] 

// Marker trait for anything that boils down to String, e.g. Char, UUID, etc.
trait Stringish /* TODO {
  this: TypeAdapter[_] =>
}*/

// Marker trait for collections
trait Collectionish

// Marker trait for classes
trait Classish