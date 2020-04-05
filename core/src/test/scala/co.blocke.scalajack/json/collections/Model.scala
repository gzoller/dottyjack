package co.blocke.scalajack
package json.collections

import scala.math._

case class BigDecimalArr( a1: Array[BigDecimal], a2: Array[BigDecimal] )
case class BigIntArr( a1: Array[BigInt], a2: Array[BigInt] )
case class BooleanArr( a1: Array[Boolean], a2: Array[Boolean] )
case class ByteArr( a1: Array[Byte], a2: Array[Byte] )
case class CharArr( a1: Array[Char], a2: Array[Char] )
case class DoubleArr( a1: Array[Double], a2: Array[Double] )
case class FloatArr( a1: Array[Float], a2: Array[Float] )
case class IntArr( a1: Array[Int], a2: Array[Int] )
case class LongArr( a1: Array[Long], a2: Array[Long] )
case class ShortArr( a1: Array[Short], a2: Array[Short] )
case class StringArr( a1: Array[String], a2: Array[String] )

case class MultiArr( a0: Array[Array[Boolean]], a1: Array[Array[Array[Long]]], a2: Array[Array[BigInt]] )
case class ClassArr( a1: Array[IntArr] )
