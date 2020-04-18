package co.blocke.dottyjack
package typeadapter

import model._

import co.blocke.dotty_reflection.Clazzes._
import co.blocke.dotty_reflection.info._
import co.blocke.dotty_reflection._

import org.apache.commons.codec.binary.Base64
import scala.collection.mutable
import scala.language.implicitConversions


object BigDecimalTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[BigDecimal] with ScalarTypeAdapter[BigDecimal]:
  def matches(concrete: RType): Boolean = 
    concrete match {
      case u: UnknownInfo if u.infoClass.getName == "scala.math.BigDecimal" => true
      case _ => false
    }
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[BigDecimal] = this

  val info = Reflector.reflectOn[scala.math.BigDecimal]
  def read(parser: Parser): BigDecimal = 
    parser.expectNumber(true) match {
      case null => null
      case bd   => BigDecimal(bd)
    }
  def write[WIRE](t: BigDecimal, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    writer.writeDecimal(t, out)


object BigIntTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[BigInt] with ScalarTypeAdapter[BigInt]:
  def matches(concrete: RType): Boolean = 
    concrete match {
      case u: UnknownInfo if u.infoClass.getName == "scala.math.BigInt" => true
      case _ => false
    }
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[BigInt] = this

  val info = Reflector.reflectOn[scala.math.BigInt]
  def read(parser: Parser): BigInt = 
    parser.expectNumber(true) match {
      case null => null
      case bd   => BigInt(bd)
    }
  def write[WIRE](t: BigInt, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit = t match {
    case null => writer.writeNull(out)
    case _    => writer.writeBigInt(t, out)
  }


object BinaryTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[Array[Byte]] with ScalarTypeAdapter[Array[Byte]]:
  def matches(concrete: RType): Boolean = 
    concrete match {
      case ArrayInfo("[B", _, PrimitiveType.Scala_Byte) => true
      case c => false
    }
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[Array[Byte]] = this

  val info = Reflector.reflectOn[Array[Byte]]
  def read(parser: Parser): Array[Byte] =
    parser.expectString() match {
      case null      => null
      case s: String => Base64.decodeBase64(s)
    }

  def write[WIRE](t: Array[Byte], writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    t match {
      case null => writer.writeNull(out)
      case _    => writer.writeString(Base64.encodeBase64String(t), out)
    }


object BooleanTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[Boolean] with ScalarTypeAdapter[Boolean]:
  def matches(concrete: RType): Boolean = concrete == PrimitiveType.Scala_Boolean
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[Boolean] = this

  val info = Reflector.reflectOn[Boolean]
  def read(parser: Parser): Boolean = parser.expectBoolean()
  def write[WIRE](t: Boolean, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    writer.writeBoolean(t, out)


object ByteTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[Byte] with ScalarTypeAdapter[Byte]:
  def matches(concrete: RType): Boolean = concrete == PrimitiveType.Scala_Byte
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[Byte] = this

  val info = Reflector.reflectOn[Byte]
  def read(parser: Parser): Byte =
    Option(parser.expectNumber())
      .flatMap(_.toByteOption)
      .getOrElse {
        parser.backspace()
        throw new ScalaJackError(
          parser.showError("Cannot parse an Byte from value")
        )
      }
  def write[WIRE](t: Byte, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    writer.writeInt(t, out)


object CharTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[Char] with ScalarTypeAdapter[Char] with Stringish:
  def matches(concrete: RType): Boolean = concrete == PrimitiveType.Scala_Char
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[Char] = this

  val info = Reflector.reflectOn[Char]
  def read(parser: Parser): Char =
    parser.expectString() match {
      case null =>
        parser.backspace()
        throw new ScalaJackError(
          parser.showError("A Char typed value cannot be null")
        )
      case "" =>
        parser.backspace()
        throw new ScalaJackError(
          parser.showError("Tried to read a Char but empty string found")
        )
      case s => s.charAt(0)
    }
  def write[WIRE](t: Char, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    writer.writeString(t.toString, out)


object DoubleTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[Double] with ScalarTypeAdapter[Double]:
  def matches(concrete: RType): Boolean = concrete == PrimitiveType.Scala_Double
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[Double] = this

  val info = Reflector.reflectOn[Double]
  def read(parser: Parser): Double =
    Option(
      parser
        .expectNumber())
      .flatMap(_.toDoubleOption)
      .getOrElse {
        parser.backspace()
        throw new ScalaJackError(
          parser.showError("Cannot parse an Double from value")
        )
      }
  def write[WIRE](t: Double, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    writer.writeDouble(t, out)


object FloatTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[Float] with ScalarTypeAdapter[Float]:
  def matches(concrete: RType): Boolean = concrete == PrimitiveType.Scala_Float
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[Float] = this

  val info = Reflector.reflectOn[Float]
  def read(parser: Parser): Float =
    Option(
      parser
        .expectNumber())
      .flatMap(_.toFloatOption)
      .getOrElse {
        parser.backspace()
        throw new ScalaJackError(
          parser.showError("Cannot parse an Float from value")
        )
      }
  def write[WIRE](t: Float, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    writer.writeDouble(util.FixFloat.capFloat(t), out)


object IntTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[Int] with ScalarTypeAdapter[Int]:
  def matches(concrete: RType): Boolean = concrete == PrimitiveType.Scala_Int
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[Int] = this

  val info = Reflector.reflectOn[Int]
  def read(parser: Parser): Int =
    Option(
      parser
        .expectNumber())
      .flatMap(_.toIntOption)
      .getOrElse {
        parser.backspace()
        throw new ScalaJackError(
          parser.showError("Cannot parse an Int from value")
        )
      }
  def write[WIRE](t: Int, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    writer.writeInt(t, out)


object LongTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[Long] with ScalarTypeAdapter[Long]:
  def matches(concrete: RType): Boolean = concrete == PrimitiveType.Scala_Long
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[Long] = this

  val info = Reflector.reflectOn[Long]
  def read(parser: Parser): Long =
    Option(
      parser
        .expectNumber())
      .flatMap(_.toLongOption)
      .getOrElse {
        parser.backspace()
        throw new ScalaJackError(
          parser.showError("Cannot parse an Long from value")
        )
      }
  def write[WIRE](t: Long, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    writer.writeLong(t, out)


object ShortTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[Short] with ScalarTypeAdapter[Short]:
  def matches(concrete: RType): Boolean = concrete == PrimitiveType.Scala_Short
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[Short] = this

  val info = Reflector.reflectOn[Short]
  def read(parser: Parser): Short =
    Option(
      parser
        .expectNumber())
      .flatMap(_.toShortOption)
      .getOrElse {
        parser.backspace()
        throw new ScalaJackError(
          parser.showError("Cannot parse an Short from value")
        )
      }
  def write[WIRE](t: Short, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    writer.writeInt(t, out)


object StringTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[String] with ScalarTypeAdapter[String] with Stringish:
  def matches(concrete: RType): Boolean = concrete == PrimitiveType.Scala_String
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[String] = this

  val info = Reflector.reflectOn[String]
  def read(parser: Parser): String = parser.expectString()
  def write[WIRE](t: String, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    writer.writeString(t, out)
