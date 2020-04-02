package co.blocke.dottyjack
package typeadapter

import model._

import scala.collection.mutable
import scala.util.{Try, Success, Failure}
import java.lang.reflect.Method
import co.blocke.dotty_reflection._
import co.blocke.dotty_reflection.infos._

object EnumTypeAdapterFactory extends TypeAdapterFactory:
  def matches(concrete: ConcreteType): Boolean = 
    concrete match {
      case _: ScalaEnumInfo => true
      case _: JavaEnumInfo => true
      case _ => false
    }

  def makeTypeAdapter(concrete: ConcreteType)(implicit taCache: TypeAdapterCache): TypeAdapter[_] =
    val enumsAsInt = taCache.jackFlavor.enumsAsInt
    concrete match {
      // Scala 2.x Enumeration support
      case scalaOld: ScalaEnumeration => 
        val erasedEnumClassName = scalaOld.name + "$"
        val enumInstance = Class
          .forName(erasedEnumClassName)
          .getField(scala.reflect.NameTransformer.MODULE_INSTANCE_NAME)
          .get(null)
          .asInstanceOf[Enumeration]
        ScalaEnumerationTypeAdapter(enumInstance, concrete, enumsAsInt)

      // Scala 3.x Enum support
      case scalaNew: ScalaEnum => 
        ScalaEnumTypeAdapter(concrete, enumsAsInt)

      // Java Enum support
      case javaEnum: JavaEnumInfo => ???  // TODO
    }


case class ScalaEnumerationTypeAdapter[E <: Enumeration](
    e:           E,
    info:        ConcreteType,
    enumsAsInt:  Boolean
  ) extends TypeAdapter[e.Value]:

  def read(parser: Parser): e.Value = 
    if (parser.nextIsNumber) {
      val en = parser.expectNumber()
      Try(e(en.toInt)) match {
        case Success(u) => u
        case Failure(u) =>
          parser.backspace()
          throw new ScalaJackError(
            parser.showError(
              s"No value found in enumeration ${e.getClass.getName} for $en"
            )
          )
      }
    } else if (parser.nextIsString) {
      val es = parser.expectString()
      if (es == null)
        null
      else
        Try(e.withName(es)) match {
          case Success(u) => u
          case Failure(u) =>
            parser.backspace()
            throw new ScalaJackError(
              parser.showError(
                s"No value found in enumeration ${e.getClass.getName} for $es"
              )
            )
        }
    } else if (parser.peekForNull)
      null
    else
      throw new ScalaJackError(
        parser.showError(s"Expected a Number or String here")
      )

  def write[WIRE](
      t:      e.Value,
      writer: Writer[WIRE],
      out:    mutable.Builder[WIRE, WIRE]): Unit =
    t match {
      case null            => writer.writeNull(out)
      case v if enumsAsInt => writer.writeInt(v.id, out)
      case v               => writer.writeString(v.toString, out)
    }


case class ScalaEnumTypeAdapter[E <: Enum](
    info:        ConcreteType,
    enumsAsInt:  Boolean
  ) extends TypeAdapter[E]:

  val scalaEnum = info.asInstanceOf[ScalaEnum]
  
  def read(parser: Parser): E = 
    if (parser.nextIsNumber) {
      val en = parser.expectNumber().toInt
      Try(scalaEnum.values(en-1)) match {
        case Success(u) => u.asInstanceOf[E]
        case Failure(u) =>
          parser.backspace()
          throw new ScalaJackError(
            parser.showError(
              s"No value found in enumeration ${info.name} for $en"
            )
          )
      }
    } else if (parser.nextIsString) {
      val es = parser.expectString()
      if (es == null)
        null.asInstanceOf[E]
      else
        Try(scalaEnum.valueOf(es).asInstanceOf[E]) match {
          case Success(u) => u
          case Failure(u) =>
            parser.backspace()
            throw new ScalaJackError(
              parser.showError(
                s"No value found in enumeration ${info.name} for $es"
              )
            )
        }
    } else if (parser.peekForNull)
      null.asInstanceOf[E]
    else
      throw new ScalaJackError(
        parser.showError(s"Expected a Number or String here")
      )

  def write[WIRE](
      t:      E,
      writer: Writer[WIRE],
      out:    mutable.Builder[WIRE, WIRE]): Unit = 
    t match {
      case null            => writer.writeNull(out)
      case _ if enumsAsInt => writer.writeInt(t.ordinal+1, out)
      case _               => writer.writeString(t.toString, out)
    }
  