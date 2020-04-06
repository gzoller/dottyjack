package co.blocke.scalajack
package json.collections

import scala.math._
import scala.collection.Map
import scala.collection.Seq

//------ Arrays
case class BigDecimalArr( a1: Array[BigDecimal], a2: Array[BigDecimal] )
case class BigIntArr( a1: Array[BigInt], a2: Array[BigInt] )
case class BooleanArr( a1: Array[Boolean], a2: Array[Boolean] )
case class CharArr( a1: Array[Char], a2: Array[Char] )
case class DoubleArr( a1: Array[Double], a2: Array[Double] )
case class FloatArr( a1: Array[Float], a2: Array[Float] )
case class IntArr( a1: Array[Int], a2: Array[Int] )
case class LongArr( a1: Array[Long], a2: Array[Long] )
case class ShortArr( a1: Array[Short], a2: Array[Short] )
case class StringArr( a1: Array[String], a2: Array[String] )

case class MultiArr( a0: Array[Array[Boolean]], a1: Array[Array[Array[Long]]], a2: Array[Array[BigInt]] )
case class ClassArr( a1: Array[IntArr] )

case class ListArr( a1: Array[List[Int]])
case class SetArr( a1: Array[Set[Int]])
case class MapArr( a1: Array[Map[String,Int]])

//------ Seqs
case class BigDecimalSeq( a1: Seq[BigDecimal], a2: Seq[BigDecimal] )
case class BigIntSeq( a1: Seq[BigInt], a2: Seq[BigInt] )
case class BooleanSeq( a1: Seq[Boolean], a2: Seq[Boolean] )
case class ByteSeq( a1: Seq[Byte], a2: Seq[Byte] )
case class CharSeq( a1: Seq[Char], a2: Seq[Char] )
case class DoubleSeq( a1: Seq[Double], a2: Seq[Double] )
case class FloatSeq( a1: Seq[Float], a2: Seq[Float] )
case class IntSeq( a1: Seq[Int], a2: Seq[Int] )
case class LongSeq( a1: Seq[Long], a2: Seq[Long] )
case class ShortSeq( a1: Seq[Short], a2: Seq[Short] )
case class StringSeq( a1: Seq[String], a2: Seq[String] )

case class MultiSeq( a0: Seq[Seq[Boolean]], a1: Seq[Seq[Seq[Long]]], a2: Seq[Seq[BigInt]] )
case class ClassSeq( a1: Seq[IntArr] )

case class SeqSeq( a1: Seq[List[Int]])
case class MapSeq( a1: Seq[Map[String,Int]])

//------ Maps (Some "2" variants to avoid same-name collision with Scala collection classes)
case class BigDecimalMap( a0: Map[BigDecimal,String], a1: Map[BigDecimal,String], a2: Map[String,BigDecimal] )
case class BigIntMap( a0: Map[BigInt,String], a1: Map[BigInt,String], a2: Map[String,BigInt] )
case class BooleanMap( a0: Map[Boolean,String], a1: Map[Boolean,String], a2: Map[String,Boolean] )
case class ByteMap( a0: Map[Byte,String], a1: Map[Byte,String], a2: Map[String,Byte] )
case class CharMap( a0: Map[Char,String], a1: Map[Char,String], a2: Map[String,Char] )
case class DoubleMap( a0: Map[Double,String], a1: Map[Double,String], a2: Map[String,Double] )
case class FloatMap( a0: Map[Float,String], a1: Map[Float,String], a2: Map[String,Float] )
case class IntMap2( a0: Map[Int,String], a1: Map[Int,String], a2: Map[String,Int] )
case class LongMap2( a0: Map[Long,String], a1: Map[Long,String], a2: Map[String,Long] )
case class ShortMap2( a0: Map[Short,String], a1: Map[Short,String], a2: Map[String,Short] )

case class MultiMap( a1: Map[Map[String,Boolean],Int], a2: Map[Int,Map[String,Boolean]] )
case class ClassMap( a1: Map[String,IntArr], a2: Map[IntArr,String] )
case class ArrayMap( a1: Map[String,Array[Int]], a2:Map[Array[Int],String])
case class SeqMap2( a1: Map[String,Seq[Int]], a2: Map[Seq[Int],String])