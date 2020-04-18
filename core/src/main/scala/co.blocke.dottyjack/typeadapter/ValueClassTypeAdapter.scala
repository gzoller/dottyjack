package co.blocke.dottyjack
package typeadapter

import model._
import co.blocke.dotty_reflection.info._
import co.blocke.dotty_reflection._
import scala.collection.mutable

object ValueClassTypeAdapterFactory extends TypeAdapterFactory:
  def matches(concrete: RType): Boolean = concrete match {
    case c: ScalaClassInfo if c.isValueClass => true
    case _ => false
  }

  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[_] =
    val elementType = concrete.asInstanceOf[ScalaClassInfo].fields(0).fieldType
    ValueClassTypeAdapter(concrete, taCache.typeAdapterOf(elementType))


case class ValueClassTypeAdapter[VC, Value](
    info:               RType,
    elementTypeAdapter: TypeAdapter[Value]
) extends TypeAdapter[VC] {
  private val vcInfo = info.asInstanceOf[ScalaClassInfo]
  def read(parser: Parser): VC = vcInfo.constructWith[VC](List(elementTypeAdapter.read(parser).asInstanceOf[Object]))
  def write[WIRE](
      t:      VC,
      writer: Writer[WIRE],
      out:    mutable.Builder[WIRE, WIRE]): Unit =
    elementTypeAdapter.write(vcInfo.fields(0).asInstanceOf[ScalaFieldInfo].valueOf(t.asInstanceOf[Object]).asInstanceOf[Value], writer, out)
}
