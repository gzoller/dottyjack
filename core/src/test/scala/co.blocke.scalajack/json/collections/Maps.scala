package co.blocke.scalajack
package json.collections

import co.blocke.dotty_reflection._
import scala.math._
import java.util.UUID
import TestUtil._
import munit._
import munit.internal.console
import co.blocke.dottyjack.json.JSON
import scala.collection.immutable._

class Maps() extends FunSuite:

  val sj = co.blocke.dottyjack.DottyJack()

  test("BigDecimal must work") {
    describe("---------------------\n:  Scala Map Tests  :\n---------------------", Console.BLUE)
    describe("+++ Primitive Types +++")

    val inst = BigDecimalMap(null, Map(BigDecimal(123.456)->"a",BigDecimal(78.91)->"b"), Map("a"->BigDecimal(123.456),"b"->BigDecimal(78.91)))
    val js = sj.render(inst)
    assertEquals(
      """{"a0":null,"a1":{"123.456":"a","78.91":"b"},"a2":{"a":123.456,"b":78.91}}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[BigDecimalMap](js))
  }

  test("BigInt must work") {
    val inst = BigIntMap(null, Map(BigInt(123)->"a",BigInt(456)->"b"), Map("a"->BigInt(789),"b"->BigInt(321)))
    val js = sj.render(inst)
    assertEquals(
      """{"a0":null,"a1":{"123":"a","456":"b"},"a2":{"a":789,"b":321}}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[BigIntMap](js))
  }

  test("Boolean must work") {
    val inst = BooleanMap(null, Map(true->"a",false->"b"), Map("a"->true,"b"->false))
    val js = sj.render(inst)
    assertEquals(
      """{"a0":null,"a1":{"true":"a","false":"b"},"a2":{"a":true,"b":false}}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[BooleanMap](js))
  }

  test("Byte must work") {
    val inst = ByteMap(null, Map(250.toByte->"a",200.toByte->"b"), Map("a"->150.toByte,"b"->100.toByte))
    val js = sj.render(inst)
    assertEquals(
      """{"a0":null,"a1":{"-6":"a","-56":"b"},"a2":{"a":-106,"b":100}}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[ByteMap](js))
  }

  test("Char must work") {
    val inst = CharMap(null, Map('t'->"a",'u'->"b"), Map("a"->'v',"b"->'w'))
    val js = sj.render(inst)
    assertEquals(
      """{"a0":null,"a1":{"t":"a","u":"b"},"a2":{"a":"v","b":"w"}}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[CharMap](js))
  }

  test("Double must work") {
    val inst = DoubleMap(null, Map(12.34->"a",45.67->"b"), Map("a"->67.89,"b"->1923.432))
    val js = sj.render(inst)
    assertEquals(
      """{"a0":null,"a1":{"12.34":"a","45.67":"b"},"a2":{"a":67.89,"b":1923.432}}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[DoubleMap](js))
  }

  test("Float must work") {
    val inst = FloatMap(null, Map(12.34F->"a",45.67F->"b"), Map("a"->67.89F,"b"->1923.432F))
    val js = sj.render(inst)
    assertEquals(
      """{"a0":null,"a1":{"12.34":"a","45.67":"b"},"a2":{"a":67.89,"b":1923.432}}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[FloatMap](js))
  }

  test("Int must work") {
    val inst = IntMap2(null, Map(12->"a",-45->"b"), Map("a"->67,"b"->1923))
    val js = sj.render(inst)
    assertEquals(
      """{"a0":null,"a1":{"12":"a","-45":"b"},"a2":{"a":67,"b":1923}}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[IntMap2](js))
  }

  test("Long must work") {
    val inst = LongMap2(null, Map(12L->"a",-45L->"b"), Map("a"->67L,"b"->1923L))
    val js = sj.render(inst)
    assertEquals(
      """{"a0":null,"a1":{"12":"a","-45":"b"},"a2":{"a":67,"b":1923}}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[LongMap2](js))
  }

  test("Short must work") {
    val inst = ShortMap2(null, Map(12.toShort->"a",-45.toShort->"b"), Map("a"->67.toShort,"b"->19.toShort))
    val js = sj.render(inst)
    assertEquals(
      """{"a0":null,"a1":{"12":"a","-45":"b"},"a2":{"a":67,"b":19}}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[ShortMap2](js))
  }

  test("Arrays must work") {
    describe("+++ Collection Types +++")
    val inst = ArrayMap( Map("a"->Array(1,2,3),"b"->Array(4,5,6)), Map(Array(1,2,3)->"a",Array(4,5,6)->"b") )
    val js = sj.render(inst)
    assertEquals(
      """{"a1":{"a":[1,2,3],"b":[4,5,6]},"a2":{"[1,2,3]":"a","[4,5,6]":"b"}}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[ArrayMap](js))
  }

  test("Maps must work") {
    val inst = SeqMap2( Map("a"->List(1,2,3),"b"->List(4,5,6)), Map(List(1,2,3)->"a",List(4,5,6)->"b") )
    val js = sj.render(inst)
    assertEquals(
      """{"a1":{"a":[1,2,3],"b":[4,5,6]},"a2":{"[1,2,3]":"a","[4,5,6]":"b"}}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[SeqMap2](js))
  }

  test("Classes must work") {
    describe("+++ Class Types +++")
    val inst = ClassMap(Map("a"->IntArr(Array(1,2),null),"b"->IntArr(Array(3,4),null)), Map(IntArr(Array(1,2),null)->"a",IntArr(Array(3,4),null)->"b"))
    val js = sj.render(inst)
    assertEquals(
      """{"a1":{"a":{"a1":[1,2],"a2":null},"b":{"a1":[3,4],"a2":null}},"a2":{"{\"a1\":[1,2],\"a2\":null}":"a","{\"a1\":[3,4],\"a2\":null}":"b"}}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[ClassMap](js))
  }

  test("Multidimensional arrays must work") {
    describe("+++ Complex Types +++")
    val inst = MultiMap( Map( Map("a"->true,"b"->false)->1, Map("c"->true,"d"->false)->2), Map( 3->Map("a"->true,"b"->false), 4->Map("c"->true,"d"->false)) )
    val js = sj.render(inst)
    assertEquals(
      """{"a1":{"{\"a\":true,\"b\":false}":1,"{\"c\":true,\"d\":false}":2},"a2":{"3":{"a":true,"b":false},"4":{"c":true,"d":false}}}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[MultiMap](js))
  }

  test("BigDecimal must work") {
    describe("--------------------\n:  Java Map Tests  :\n--------------------", Console.BLUE)
    describe("+++ Primitive Types +++")
    pending
  }

  test("Lists must work") {
    describe("+++ Collection Types +++")
    pending
  }

  test("Maps must work") {
    pending
  }

  test("Classes must work") {
    describe("+++ Class Types +++")
    pending
  }

  test("Multidimensional arrays must work") {
    describe("+++ Complex Types +++")
    pending
  }