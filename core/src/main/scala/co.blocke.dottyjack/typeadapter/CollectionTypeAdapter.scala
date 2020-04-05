package co.blocke.dottyjack
package typeadapter

import model._

import java.lang.reflect.Method
import scala.collection.mutable
import co.blocke.dotty_reflection._
import co.blocke.dotty_reflection.infos._
import scala.reflect.ClassTag

object CollectionTypeAdapterFactory extends TypeAdapterFactory:
  def matches(concrete: ConcreteType): Boolean = 
    concrete match {
      case _: CollectionType => true
      case _ => false
    }
  def makeTypeAdapter(concrete: ConcreteType)(implicit taCache: TypeAdapterCache): TypeAdapter[_] =
    concrete match {
      case c: SeqLikeInfo => 
        val elementInfo = c.elementType.asInstanceOf[ConcreteType]
        val companionClass = Class.forName(c.infoClass.getName+"$")
        val companionInstance = companionClass.getField("MODULE$").get(companionClass)
        val builderMethod = companionClass.getMethod("newBuilder")
        SeqLikeTypeAdapter(concrete, elementInfo.isInstanceOf[OptionInfo], taCache.typeAdapterOf(elementInfo), companionInstance, builderMethod)
      // case c: MapLikeInfo =>
      // case c: JavaSetInfo =>
      // case c: JavaListInfo =>
      // case c: JavaQueueInfo =>
      // case c: JavaMapInfo =>
    }


case class SeqLikeTypeAdapter[ELEM, TO](
    info:               ConcreteType,
    elemIsOptional:     Boolean,
    elementTypeAdapter: TypeAdapter[ELEM],
    companionInstance:  Object,
    builderMethod:      Method
  ) extends TypeAdapter[TO]:

  def read(parser: Parser): TO =
    // We have to do some voodoo here and peek ahead for Null.  Some types, e.g. Int, aren't nullable,
    // but Option[Int] is nullable, so we can't trust the valueTypeAdapter to catch and handle null in
    // these cases.
    parser.peekForNull match {
      case true               => null.asInstanceOf[TO]
      case _                  => 
        val builder = builderMethod.invoke(companionInstance).asInstanceOf[mutable.Builder[ELEM,TO]]
        parser.expectList(
          elementTypeAdapter,
          builder
        )
        builder.result
    }

  def write[WIRE](
      t:      TO,
      writer: Writer[WIRE],
      out:    mutable.Builder[WIRE, WIRE]): Unit =
    t match {
      case null                => writer.writeNull(out)
      case _ if elemIsOptional =>
        writer.writeArray(
          t.asInstanceOf[Iterable[ELEM]].filterNot(_ == None),
          elementTypeAdapter,
          out
        )
      case _ =>
        writer.writeArray(t.asInstanceOf[Iterable[ELEM]], elementTypeAdapter, out)
    }
