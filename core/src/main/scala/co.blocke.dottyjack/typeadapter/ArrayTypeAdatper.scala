package co.blocke.dottyjack
package typeadapter

import model._

import co.blocke.dotty_reflection.impl.Clazzes._
import co.blocke.dotty_reflection.infos._
import co.blocke.dotty_reflection._

import scala.collection.mutable
import scala.language.implicitConversions


object ArrayTypeAdapterFactory extends TypeAdapterFactory:

  // trait HasInfoClass {
  //   def infoClass: Class[_]
  // }

  def matches(concrete: ConcreteType): Boolean = 
    concrete match {
      case _: ArrayInfo => true
      case _ => false
    }

  def makeTypeAdapter(concrete: ConcreteType)(implicit taCache: TypeAdapterCache): TypeAdapter[_] = 
    val elementInfo = concrete.asInstanceOf[ArrayInfo].elementType.asInstanceOf[ConcreteType]
    ArrayTypeAdapter(
      concrete, 
      concrete.infoClass, 
      elementInfo.isInstanceOf[OptionInfo],
      taCache.typeAdapterOf(elementInfo))


case class ArrayTypeAdapter[ELEM](
    info:               ConcreteType,
    elementClass:       Class[_],
    elemIsOptional:     Boolean,
    elementTypeAdapter: TypeAdapter[ELEM]
  ) extends TypeAdapter[Array[ELEM]] with ScalarTypeAdapter[Array[ELEM]]:

  val arrayInfo = info.asInstanceOf[ArrayInfo]

  def read(parser: Parser): Array[ELEM] = 
    val classtag = scala.reflect.ClassTag[ELEM](arrayInfo.elementType.asInstanceOf[ConcreteType].infoClass)
    val builder: mutable.Builder[ELEM,Array[ELEM]] = Array.newBuilder[ELEM](classtag).asInstanceOf[mutable.Builder[ELEM,Array[ELEM]]]
    val values = parser.expectList(elementTypeAdapter, builder)
    builder.result

  def write[WIRE](t: Array[ELEM], writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit = 
    if (elemIsOptional)
      writer.writeArray(
        t.toList.filterNot(_ == None),
        elementTypeAdapter,
        out
      )
    else
      writer.writeArray(t.toList, elementTypeAdapter, out)
