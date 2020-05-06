package co.blocke.dottyjack
package typeadapter

import model._
import co.blocke.dotty_reflection.info._
import co.blocke.dotty_reflection._
import scala.collection.mutable

object ValueClassTypeAdapterFactory extends TypeAdapterFactory:
  def matches(concrete: RType): Boolean = concrete match {
    case c: ScalaCaseClassInfo if c.isValueClass => true
    case c: ScalaClassInfo if c.isValueClass => true
    case _ => false
  }

  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[_] =
    val elementType = concrete.asInstanceOf[ClassInfo].fields(0).fieldType
    ValueClassTypeAdapter(concrete, taCache.typeAdapterOf(elementType))


case class ValueClassTypeAdapter[VC, Value](
    info:               RType,
    elementTypeAdapter: TypeAdapter[Value]
    // wrap:               (Object) => VC,
    // unwrap:             (AnyVal) => Object
) extends TypeAdapter[VC] {

  def read(parser: Parser): VC = 
    info.asInstanceOf[ClassInfo].constructWith(List(elementTypeAdapter.read(parser).asInstanceOf[Object])).asInstanceOf[VC]
    // PROBLEM:  Constructor is Thing(double) not Thing(VCDouble).  Need to fix in CaseClassAdapter?  Not sure, but not here!  This is right.
    // public co.blocke.scalajack.json.plainclass.One(double)

  def write[WIRE](
      t:      VC,
      writer: Writer[WIRE],
      out:    mutable.Builder[WIRE, WIRE]): Unit =
    println("T: "+t)

    /*
    Sometimes VCs are instantiated, sometimes left primitive.  How can we tell?  Handle accordingly!
    */
    /*
    t match {
      case x: VC => println("Value Class! "+x.getClass)
      case _ => println("nope...")
    }
    println(t.getClass)
    // PROBLEM:  ScalaFieldInfo's valueAccessor is an accessor method on the *Class*.  t is an instance of the field, not the class. :-(
    elementTypeAdapter.write(vcInfo.fields(0).asInstanceOf[ScalaFieldInfo].valueOf(t.asInstanceOf[Object]).asInstanceOf[Value], writer, out)
    */
}


// TODO: Try plan/case classes with Value Class as a constructor argument