package co.blocke.dottyjack
package typeadapter

import model._

import co.blocke.dotty_reflection.impl.Clazzes._
import co.blocke.dotty_reflection.infos._
import co.blocke.dotty_reflection._

import scala.collection.mutable
import scala.reflect.ClassTag
import scala.language.implicitConversions


object ArrayTypeAdapterFactory extends TypeAdapterFactory:

  def matches(concrete: ConcreteType): Boolean = 
    concrete match {
      case _: ArrayInfo => true
      case _ => false
    }

  def makeTypeAdapter(concrete: ConcreteType)(implicit taCache: TypeAdapterCache): TypeAdapter[_] = 
    val elementInfo = concrete.asInstanceOf[ArrayInfo].elementType.asInstanceOf[ConcreteType]
    ArrayTypeAdapter(
      concrete, 
      elementInfo.isInstanceOf[OptionInfo],
      taCache.typeAdapterOf(elementInfo))


case class ArrayTypeAdapter[ELEM](
    info:               ConcreteType,
    elemIsOptional:     Boolean,
    elementTypeAdapter: TypeAdapter[ELEM]
  ) extends TypeAdapter[Array[ELEM]] with ScalarTypeAdapter[Array[ELEM]]:

  val arrayInfo = info.asInstanceOf[ArrayInfo]

  def read(parser: Parser): Array[ELEM] = 
    parser.peekForNull match {
      case true               => null
      case _                  => 
        val classtag = ClassTag[ELEM](arrayInfo.elementType.asInstanceOf[ConcreteType].infoClass)
        val builder: mutable.Builder[ELEM,Array[ELEM]] = Array.newBuilder[ELEM](classtag.asInstanceOf[ClassTag[ELEM]]).asInstanceOf[mutable.Builder[ELEM,Array[ELEM]]]
        val values = parser.expectList(elementTypeAdapter, builder)
        builder.result
    }

  def write[WIRE](t: Array[ELEM], writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit = 
    t match {
      case null                => writer.writeNull(out)
      case _ if elemIsOptional => 
        writer.writeArray(
          t.toList.filterNot(_ == None),
          elementTypeAdapter,
          out
        )
      case _ =>
        writer.writeArray(t.toList, elementTypeAdapter, out)
    }
