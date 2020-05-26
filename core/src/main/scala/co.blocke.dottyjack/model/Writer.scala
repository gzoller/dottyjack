package co.blocke.dottyjack
package model

import scala.collection.Map
import scala.collection.mutable
import co.blocke.dotty_reflection.info.FieldInfo

case class ExtraFieldValue[T](value: T, valueTypeAdapter: TypeAdapter[T])

trait Writer[WIRE] {
  def writeArray[Elem](t: Iterable[Elem], elemTypeAdapter: TypeAdapter[Elem], out: mutable.Builder[WIRE, WIRE]): Unit
  def writeBigInt(t: BigInt, out: mutable.Builder[WIRE, WIRE]): Unit
  def writeBoolean(t: Boolean, out: mutable.Builder[WIRE, WIRE]): Unit
  def writeDecimal(t: BigDecimal, out: mutable.Builder[WIRE, WIRE]): Unit
  def writeDouble(t: Double, out: mutable.Builder[WIRE, WIRE]): Unit
  def writeInt(t: Int, out: mutable.Builder[WIRE, WIRE]): Unit
  def writeLong(t: Long, out: mutable.Builder[WIRE, WIRE]): Unit
  def writeMap[Key, Value, To](t: Map[Key, Value], keyTypeAdapter: TypeAdapter[Key], valueTypeAdapter: TypeAdapter[Value], out: mutable.Builder[WIRE, WIRE]): Unit
  def writeNull(out: mutable.Builder[WIRE, WIRE]): Unit
  def writeObject[T](
      t: T,
      orderedFieldNames: List[String],
      fieldMembersByName: Map[String, ClassFieldMember[_,_]],
      out: mutable.Builder[WIRE, WIRE],
      extras: List[(String, ExtraFieldValue[_])] = List.empty[(String, ExtraFieldValue[_])]
  ): Unit
  def writeString(t: String, out: mutable.Builder[WIRE, WIRE]): Unit
  def writeRaw(t: WIRE, out: mutable.Builder[WIRE, WIRE]): Unit // i.e. no quotes for JSON
  def writeTuple[T](
      t: T,
      writeFns: List[TupleField[_]],
      out: mutable.Builder[WIRE, WIRE]
  ): Unit
}