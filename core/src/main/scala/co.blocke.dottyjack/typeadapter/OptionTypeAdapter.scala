package co.blocke.dottyjack
package typeadapter

import model._

import scala.collection.mutable
import co.blocke.dotty_reflection._
import co.blocke.dotty_reflection.infos._

/*
 O, the exquisite pain of mapping Option (None) to something in JSON!
 Our assumptions (which may be different from earlier versions of ScalaJack:

 * Normal: None is just missing.  Doesn't read or write anything at all.  Classic example is a class field value--it's
     just "missing" from the JSON and understood to be None.

 * Map key fields: Element is dropped from Map, like List element behavior

 * Map value fields:  Element is dropped from Map, like List element behavior

 * List elements:  Normal, i.e. read/write nothing.  None elements just disappear.  NOTE this does mean that a read/render
     cycle may not yield the same object, which is generally breaking a ScalaJack core behavior goal

 * Tuple elements: Place needs to be preserved in a Tuple, so None becomes JSON null.  Really hate this option, but JSON
     doesn't leave many choices here.
 */

object OptionTypeAdapterFactory extends TypeAdapterFactory:
  def matches(concrete: ConcreteType): Boolean = 
    concrete match {
      case _: OptionInfo => true
      case _ => false
    }
  def makeTypeAdapter(concrete: ConcreteType)(implicit taCache: TypeAdapterCache): TypeAdapter[_] =
    val opti = concrete.asInstanceOf[OptionInfo]
    val wrapped = opti.optionParamType match {
      case c: ConcreteType => c
      case c => throw new ScalaJackError(s"Unexpected non-concrete type in option: ${c.getClass.getName}")
    }
    OptionTypeAdapter(concrete, taCache.typeAdapterOf(wrapped))


case class OptionTypeAdapter[E](
    info:             ConcreteType,
    valueTypeAdapter: TypeAdapter[E],
    nullIsNone:       Boolean        = false
  ) extends TypeAdapter[Option[E]]:

  override def defaultValue: Option[Option[E]] = Some(None)

  def valueIsStringish(): Boolean = valueTypeAdapter.isInstanceOf[Stringish]

  def read(parser: Parser): Option[E] =
    // We have to do some voodoo here and peek ahead for Null.  Some types, e.g. Int, aren't nullable,
    // but Option[Int] is nullable, so we can't trust the valueTypeAdapter to catch and handle null in
    // these cases.
    parser.peekForNull match {
      case true if nullIsNone => None
      case true               => null
      case _                  => Some(valueTypeAdapter.read(parser))
    }

  def write[WIRE](
      t:      Option[E],
      writer: Writer[WIRE],
      out:    mutable.Builder[WIRE, WIRE]): Unit =
    t match {
      case null               => writer.writeNull(out)
      case Some(e)            => valueTypeAdapter.write(e, writer, out)
      case None if nullIsNone => writer.writeNull(out)
      case None               =>
    }

  def convertNullToNone(): OptionTypeAdapter[E] = this.copy(nullIsNone = true)