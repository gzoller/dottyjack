package co.blocke.dottyjack
package json

import model._
import co.blocke.dotty_reflection._
import co.blocke.dotty_reflection.infos._

import scala.collection.mutable

// A TypeAdapter for a type T, which is wrapped in a String, a.k.a. "stringified".
// This is used for JSON Map keys, which must be strings.
case class StringWrapTypeAdapter[T](
    wrappedTypeAdapter: TypeAdapter[T],
    emptyStringOk:      Boolean        = true
  ) extends TypeAdapter[T] with Stringish {

  val info: ConcreteType = ??? // unused

  def read(parser: Parser): T =
    parser.expectString() match {
      case null => null.asInstanceOf[T]
      case s if s.isEmpty && !emptyStringOk =>
        parser.backspace()
        throw new ScalaJackError(
          parser.showError(s"Expected a ${wrappedTypeAdapter.info.name} here")
        )
      case s =>
        wrappedTypeAdapter.read(parser.subParser(s.asInstanceOf[parser.WIRE]))
    }

  def write[WIRE](
      t:      T,
      writer: Writer[WIRE],
      out:    mutable.Builder[WIRE, WIRE]): Unit = {
    val stringBuilder = co.blocke.dottyjack.model.StringBuilder()
    wrappedTypeAdapter.write(
      t,
      writer,
      stringBuilder.asInstanceOf[mutable.Builder[Any, WIRE]]
    )
    writer.writeString(stringBuilder.result(), out)
  }
}
