package co.blocke.dottyjack
package typeadapter

import model._
import co.blocke.dotty_reflection._
import co.blocke.dotty_reflection.info.TupleInfo
import java.lang.reflect.Method
import scala.collection.mutable
import scala.util.matching.Regex

object TupleTypeAdapterFactory extends TypeAdapterFactory:

  private val tupleFullName: Regex = """scala.Tuple(\d+)""".r

  def matches(concrete: Transporter.RType): Boolean = 
    concrete match {
      case ti: TupleInfo => true
      case _ => false
    }

  def makeTypeAdapter(concrete: Transporter.RType)(implicit taCache: TypeAdapterCache): TypeAdapter[_] =
    val ti = concrete.asInstanceOf[TupleInfo]
    val tupClassFields = ti.infoClass.getDeclaredFields
    var idx = 0
    val fields = ti.tupleTypes.zip(tupClassFields).collect{
      case (tupField: info.TypeSymbolInfo, _) => throw new ScalaJackError(s"Unexpected non-Concrete tuple type ${tupField.getClass.getName}")
      case (c: Transporter.RType, javaClassField: java.lang.reflect.Field) => 
        javaClassField.setAccessible(true)
        val typeAdapter = taCache.typeAdapterOf(c) match {
          case ta: OptionTypeAdapter[_] => ta.convertNullToNone()
          case ta: JavaOptionalTypeAdapter[_] => ta.convertNullToNone()
          case ta => ta
        }
        idx += 1
        TupleField(idx, javaClassField, typeAdapter)
      }
    TupleTypeAdapter(concrete, fields.toList, ti.infoClass.getConstructors.head)


case class TupleTypeAdapter[T](
  info:        Transporter.RType,
  fields:      List[TupleField[_]],
  constructor: java.lang.reflect.Constructor[T]
  ) extends TypeAdapter[T] with Collectionish {

  def read(parser: Parser): T =
    if (parser.peekForNull) then
      null.asInstanceOf[T]
    else
      constructor.newInstance(parser.expectTuple(fields): _*).asInstanceOf[T]

  // Create functions that know how to self-write each field.  The actual writing of each element
  // is done in TupleField where the specific field type F is known.
  def write[WIRE](
      t:      T,
      writer: Writer[WIRE],
      out:    mutable.Builder[WIRE, WIRE]): Unit =
    if (t == null)
      writer.writeNull(out)
    else
      writer.writeTuple(t, fields, out)
}
