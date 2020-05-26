package co.blocke.dottyjack
package typeadapter

import model._
import co.blocke.dotty_reflection.Clazzes._
import co.blocke.dotty_reflection.info._
import co.blocke.dotty_reflection._
import scala.collection.mutable

import java.math.BigDecimal
import java.math.BigInteger


object PermissiveJavaBigDecimalTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[BigDecimal] with ScalarTypeAdapter[BigDecimal]:
  def matches(concrete: RType): Boolean = 
    concrete match {
      case u: JavaClassInfo if u.infoClass.getName == "java.math.BigDecimal" => true
      case _ => false
    }
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[BigDecimal] = this

  val info = Reflector.reflectOn[scala.math.BigDecimal]
  def read(parser: Parser): BigDecimal = 
    if (parser.nextIsString)
      parser.jackFlavor
        .stringWrapTypeAdapterFactory(JavaBigDecimalTypeAdapterFactory, emptyStringOk = false)
        .read(parser)
    else
      JavaBigDecimalTypeAdapterFactory.read(parser)
 
  def write[WIRE](t: BigDecimal, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    JavaBigDecimalTypeAdapterFactory.write(t, writer, out)


object PermissiveJavaBigIntegerTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[BigInteger] with ScalarTypeAdapter[BigInteger]:
  def matches(concrete: RType): Boolean = 
    concrete match {
      case u: JavaClassInfo if u.infoClass.getName == "java.math.BigInteger" => true
      case _ => false
    }
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[BigInteger] = this

  val info = Reflector.reflectOn[java.math.BigInteger]
  def read(parser: Parser): BigInteger = 
    if (parser.nextIsString)
      parser.jackFlavor
        .stringWrapTypeAdapterFactory(JavaBigIntegerTypeAdapterFactory, emptyStringOk = false)
        .read(parser)
    else
      JavaBigIntegerTypeAdapterFactory.read(parser)
 
  def write[WIRE](t: BigInteger, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    JavaBigIntegerTypeAdapterFactory.write(t, writer, out)


object PermissiveJavaBooleanTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[java.lang.Boolean] with ScalarTypeAdapter[java.lang.Boolean]:
  def matches(concrete: RType): Boolean = concrete == PrimitiveType.Java_Boolean
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[java.lang.Boolean] = this

  val info = Reflector.reflectOn[java.lang.Boolean]
  def read(parser: Parser): java.lang.Boolean = 
    if (parser.nextIsString)
      parser.jackFlavor
        .stringWrapTypeAdapterFactory(JavaBooleanTypeAdapterFactory, emptyStringOk = false)
        .read(parser)
    else
      JavaBooleanTypeAdapterFactory.read(parser)
  
  def write[WIRE](t: java.lang.Boolean, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    JavaBooleanTypeAdapterFactory.write(t, writer, out)

  
object PermissiveJavaByteTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[java.lang.Byte] with ScalarTypeAdapter[java.lang.Byte]:
  def matches(concrete: RType): Boolean = concrete == PrimitiveType.Java_Byte
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[java.lang.Byte] = this

  val info = Reflector.reflectOn[java.lang.Byte]
  def read(parser: Parser): java.lang.Byte = 
    if (parser.nextIsString)
      parser.jackFlavor
        .stringWrapTypeAdapterFactory(JavaByteTypeAdapterFactory, emptyStringOk = false)
        .read(parser)
    else
      JavaByteTypeAdapterFactory.read(parser)
  
  def write[WIRE](t: java.lang.Byte, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    JavaByteTypeAdapterFactory.write(t, writer, out)


object PermissiveJavaDoubleTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[java.lang.Double] with ScalarTypeAdapter[java.lang.Double]:
  def matches(concrete: RType): Boolean = concrete == PrimitiveType.Java_Double
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[java.lang.Double] = this

  val info = Reflector.reflectOn[java.lang.Double]
  def read(parser: Parser): java.lang.Double = 
    if (parser.nextIsString)
      parser.jackFlavor
        .stringWrapTypeAdapterFactory(JavaDoubleTypeAdapterFactory, emptyStringOk = false)
        .read(parser)
    else
      JavaDoubleTypeAdapterFactory.read(parser)
  
  def write[WIRE](t: java.lang.Double, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    JavaDoubleTypeAdapterFactory.write(t, writer, out)

   
object PermissiveJavaFloatTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[java.lang.Float] with ScalarTypeAdapter[java.lang.Float]:
  def matches(concrete: RType): Boolean = concrete == PrimitiveType.Java_Float
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[java.lang.Float] = this

  val info = Reflector.reflectOn[java.lang.Float]
  def read(parser: Parser): java.lang.Float = 
    if (parser.nextIsString)
      parser.jackFlavor
        .stringWrapTypeAdapterFactory(JavaFloatTypeAdapterFactory, emptyStringOk = false)
        .read(parser)
    else
      JavaFloatTypeAdapterFactory.read(parser)
  
  def write[WIRE](t: java.lang.Float, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    JavaFloatTypeAdapterFactory.write(t, writer, out)


object PermissiveJavaIntTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[java.lang.Integer] with ScalarTypeAdapter[java.lang.Integer]:
  def matches(concrete: RType): Boolean = concrete == PrimitiveType.Java_Int
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[java.lang.Integer] = this

  val info = Reflector.reflectOn[java.lang.Integer]
  def read(parser: Parser): java.lang.Integer = 
    if (parser.nextIsString)
      parser.jackFlavor
        .stringWrapTypeAdapterFactory(JavaIntegerTypeAdapterFactory, emptyStringOk = false)
        .read(parser)
    else
      JavaIntegerTypeAdapterFactory.read(parser)
  
  def write[WIRE](t: java.lang.Integer, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    JavaIntegerTypeAdapterFactory.write(t, writer, out)


object PermissiveJavaLongTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[java.lang.Long] with ScalarTypeAdapter[java.lang.Long]:
  def matches(concrete: RType): Boolean = concrete == PrimitiveType.Java_Long
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[java.lang.Long] = this

  val info = Reflector.reflectOn[java.lang.Long]
  def read(parser: Parser): java.lang.Long = 
    if (parser.nextIsString)
      parser.jackFlavor
        .stringWrapTypeAdapterFactory(JavaLongTypeAdapterFactory, emptyStringOk = false)
        .read(parser)
    else
      JavaLongTypeAdapterFactory.read(parser)
  
  def write[WIRE](t: java.lang.Long, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    JavaLongTypeAdapterFactory.write(t, writer, out)

 
object PermissiveJavaNumberTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[java.lang.Number] with ScalarTypeAdapter[java.lang.Number]:
  def matches(concrete: RType): Boolean = concrete == PrimitiveType.Java_Number
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[java.lang.Number] = this

  val info = Reflector.reflectOn[java.lang.Number]
  def read(parser: Parser): java.lang.Number = 
    if (parser.nextIsString)
      parser.jackFlavor
        .stringWrapTypeAdapterFactory(JavaNumberTypeAdapterFactory, emptyStringOk = false)
        .read(parser)
    else
      JavaNumberTypeAdapterFactory.read(parser)
  
  def write[WIRE](t: java.lang.Number, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    JavaNumberTypeAdapterFactory.write(t, writer, out)


object PermissiveJavaShortTypeAdapterFactory extends TypeAdapterFactory with TypeAdapter[java.lang.Short] with ScalarTypeAdapter[java.lang.Short]:
  def matches(concrete: RType): Boolean = concrete == PrimitiveType.Java_Short
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[java.lang.Short] = this

  val info = Reflector.reflectOn[java.lang.Short]
  def read(parser: Parser): java.lang.Short = 
    if (parser.nextIsString)
      parser.jackFlavor
        .stringWrapTypeAdapterFactory(JavaShortTypeAdapterFactory, emptyStringOk = false)
        .read(parser)
    else
      JavaShortTypeAdapterFactory.read(parser)
  
  def write[WIRE](t: java.lang.Short, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    JavaShortTypeAdapterFactory.write(t, writer, out)
  