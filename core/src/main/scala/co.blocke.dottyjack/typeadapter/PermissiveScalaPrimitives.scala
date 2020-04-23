package co.blocke.dottyjack
package typeadapter

import model._
import co.blocke.dotty_reflection.Clazzes._
import co.blocke.dotty_reflection.info._
import co.blocke.dotty_reflection._
import scala.collection.mutable

object PermissiveBigDecimalTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[BigDecimal] with ScalarTypeAdapter[BigDecimal]:
  def matches(concrete: RType): Boolean = 
    concrete match {
      case u: UnknownInfo if u.infoClass.getName == "scala.math.BigDecimal" => true
      case _ => false
    }
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[BigDecimal] = this

  val info = Reflector.reflectOn[scala.math.BigDecimal]
  def read(parser: Parser): BigDecimal = 
    if (parser.nextIsString)
      parser.jackFlavor
        .stringWrapTypeAdapterFactory(BigDecimalTypeAdapterFactory, emptyStringOk = false)
        .read(parser)
    else
      BigDecimalTypeAdapterFactory.read(parser)
 
  def write[WIRE](t: BigDecimal, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    BigDecimalTypeAdapterFactory.write(t, writer, out)


object PermissiveBigIntTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[BigInt] with ScalarTypeAdapter[BigInt]:
  def matches(concrete: RType): Boolean = 
    concrete match {
      case u: UnknownInfo if u.infoClass.getName == "scala.math.BigInt" => true
      case _ => false
    }
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[BigInt] = this

  val info = Reflector.reflectOn[scala.math.BigInt]
  def read(parser: Parser): BigInt = 
    if (parser.nextIsString)
      parser.jackFlavor
        .stringWrapTypeAdapterFactory(BigIntTypeAdapterFactory, emptyStringOk = false)
        .read(parser)
    else
      BigIntTypeAdapterFactory.read(parser)
 
  def write[WIRE](t: BigInt, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    BigIntTypeAdapterFactory.write(t, writer, out)


object PermissiveBooleanTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[Boolean] with ScalarTypeAdapter[Boolean]:
  def matches(concrete: RType): Boolean = concrete == PrimitiveType.Scala_Boolean
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[Boolean] = this

  val info = Reflector.reflectOn[Boolean]
  def read(parser: Parser): Boolean = 
    if (parser.nextIsString)
      parser.jackFlavor
        .stringWrapTypeAdapterFactory(BooleanTypeAdapterFactory, emptyStringOk = false)
        .read(parser)
    else
      BooleanTypeAdapterFactory.read(parser)
  
  def write[WIRE](t: Boolean, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    BooleanTypeAdapterFactory.write(t, writer, out)


object PermissiveByteTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[Byte] with ScalarTypeAdapter[Byte]:
  def matches(concrete: RType): Boolean = concrete == PrimitiveType.Scala_Byte
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[Byte] = this

  val info = Reflector.reflectOn[Byte]
  def read(parser: Parser): Byte = 
    if (parser.nextIsString)
      parser.jackFlavor
        .stringWrapTypeAdapterFactory(ByteTypeAdapterFactory, emptyStringOk = false)
        .read(parser)
    else
      ByteTypeAdapterFactory.read(parser)
  
  def write[WIRE](t: Byte, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    ByteTypeAdapterFactory.write(t, writer, out)
  

object PermissiveDoubleTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[Double] with ScalarTypeAdapter[Double]:
  def matches(concrete: RType): Boolean = concrete == PrimitiveType.Scala_Double
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[Double] = this

  val info = Reflector.reflectOn[Double]
  def read(parser: Parser): Double = 
    if (parser.nextIsString)
      parser.jackFlavor
        .stringWrapTypeAdapterFactory(DoubleTypeAdapterFactory, emptyStringOk = false)
        .read(parser)
    else
      DoubleTypeAdapterFactory.read(parser)
  
  def write[WIRE](t: Double, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    DoubleTypeAdapterFactory.write(t, writer, out)

    
object PermissiveFloatTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[Float] with ScalarTypeAdapter[Float]:
  def matches(concrete: RType): Boolean = concrete == PrimitiveType.Scala_Float
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[Float] = this

  val info = Reflector.reflectOn[Float]
  def read(parser: Parser): Float = 
    if (parser.nextIsString)
      parser.jackFlavor
        .stringWrapTypeAdapterFactory(FloatTypeAdapterFactory, emptyStringOk = false)
        .read(parser)
    else
      FloatTypeAdapterFactory.read(parser)
  
  def write[WIRE](t: Float, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    FloatTypeAdapterFactory.write(t, writer, out)
  

object PermissiveIntTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[Int] with ScalarTypeAdapter[Int]:
  def matches(concrete: RType): Boolean = concrete == PrimitiveType.Scala_Int
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[Int] = this

  val info = Reflector.reflectOn[Int]
  def read(parser: Parser): Int = 
    if (parser.nextIsString)
      parser.jackFlavor
        .stringWrapTypeAdapterFactory(IntTypeAdapterFactory, emptyStringOk = false)
        .read(parser)
    else
      IntTypeAdapterFactory.read(parser)
  
  def write[WIRE](t: Int, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    IntTypeAdapterFactory.write(t, writer, out)
  

object PermissiveLongTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[Long] with ScalarTypeAdapter[Long]:
  def matches(concrete: RType): Boolean = concrete == PrimitiveType.Scala_Long
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[Long] = this

  val info = Reflector.reflectOn[Long]
  def read(parser: Parser): Long = 
    if (parser.nextIsString)
      parser.jackFlavor
        .stringWrapTypeAdapterFactory(LongTypeAdapterFactory, emptyStringOk = false)
        .read(parser)
    else
      LongTypeAdapterFactory.read(parser)
  
  def write[WIRE](t: Long, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    LongTypeAdapterFactory.write(t, writer, out)


object PermissiveShortTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[Short] with ScalarTypeAdapter[Short]:
  def matches(concrete: RType): Boolean = concrete == PrimitiveType.Scala_Short
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[Short] = this

  val info = Reflector.reflectOn[Short]
  def read(parser: Parser): Short = 
    if (parser.nextIsString)
      parser.jackFlavor
        .stringWrapTypeAdapterFactory(ShortTypeAdapterFactory, emptyStringOk = false)
        .read(parser)
    else
      ShortTypeAdapterFactory.read(parser)
  
  def write[WIRE](t: Short, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    ShortTypeAdapterFactory.write(t, writer, out)