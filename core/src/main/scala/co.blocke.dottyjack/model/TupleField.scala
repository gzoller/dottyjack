package co.blocke.dottyjack
package model

import model._
import scala.collection.mutable


case class TupleField[F](
    index:            Int,
    javaClassField:   java.lang.reflect.Field,
    valueTypeAdapter: TypeAdapter[F]):

  def valueIn(tuple: Any): F = javaClassField.get(tuple).asInstanceOf[F]
  def read(parser: Parser): Any = valueTypeAdapter.read(parser)
  def write[WIRE, T](
      tuple:  T,
      writer: Writer[WIRE],
      out:    mutable.Builder[WIRE, WIRE]): Unit =
    valueTypeAdapter.write(valueIn(tuple), writer, out)
