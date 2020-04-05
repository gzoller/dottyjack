package co.blocke.scalajack
package json.collections

import co.blocke.dotty_reflection._
import scala.math._
import java.util.UUID
import TestUtil._
import munit._
import munit.internal.console
import co.blocke.dottyjack.json.JSON

class Arrays() extends FunSuite:

  val sj = co.blocke.dottyjack.DottyJack()

  test("BigDecimal must work") {
    describe("-----------------------\n:  Scala Array Tests  :\n-----------------------", Console.BLUE)
    describe("+++ Primitive Types +++")

    val inst = BigDecimalArr(null, Array(BigDecimal(123.456),BigDecimal(78.91)))
    val js = sj.render(inst)
    assertEquals(
      """{"a1":null,"a2":[123.456,78.91]}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[BigDecimalArr](js))
  }

  test("BigInt must work") {
    val inst = BigIntArr(null, Array(BigInt(123),BigInt(78)))
    val js = sj.render(inst)
    assertEquals(
      """{"a1":null,"a2":[123,78]}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[BigIntArr](js))
  }

  test("Boolean must work") {
    val inst = BooleanArr(null, Array(true,false))
    val js = sj.render(inst)
    assertEquals(
      """{"a1":null,"a2":[true,false]}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[BooleanArr](js))
  }

  // No Array[Byte] because that's seen as binary data and treated differently
  test("Char must work") {
    val inst = CharArr(null, Array('a','b','c'))
    val js = sj.render(inst)
    assertEquals(
      """{"a1":null,"a2":["a","b","c"]}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[CharArr](js))
  }

  test("Double must work") {
    val inst = DoubleArr(null, Array(12.34,56.78))
    val js = sj.render(inst)
    assertEquals(
      """{"a1":null,"a2":[12.34,56.78]}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[DoubleArr](js))
  }

  test("Float must work") {
    val inst = FloatArr(null, Array(12.34F,56.78F))
    val js = sj.render(inst)
    assertEquals(
      """{"a1":null,"a2":[12.34,56.78]}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[FloatArr](js))
  }

  test("Int must work") {
    val inst = IntArr(null, Array(1,2,3))
    val js = sj.render(inst)
    assertEquals(
      """{"a1":null,"a2":[1,2,3]}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[IntArr](js))
  }

  test("Long must work") {
    val inst = LongArr(null, Array(1L,2L,3L))
    val js = sj.render(inst)
    assertEquals(
      """{"a1":null,"a2":[1,2,3]}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[LongArr](js))
  }

  test("Short must work") {
    val inst = ShortArr(null, Array(1.toShort,2.toShort,3.toShort))
    val js = sj.render(inst)
    assertEquals(
      """{"a1":null,"a2":[1,2,3]}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[ShortArr](js))
  }

  test("String must work") {
    val inst = StringArr(null, Array("a","b","c"))
    val js = sj.render(inst)
    assertEquals(
      """{"a1":null,"a2":["a","b","c"]}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[StringArr](js))
  }

  test("String must work") {
    val inst = StringArr(null, Array("a","b","c"))
    val js = sj.render(inst)
    assertEquals(
      """{"a1":null,"a2":["a","b","c"]}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[StringArr](js))
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
    val inst = ClassArr(Array(IntArr(null,Array(1,2)), IntArr(null,Array(1,2))))
    val js = sj.render(inst)
    assertEquals(
      """{"a1":[{"a1":null,"a2":[1,2]},{"a1":null,"a2":[1,2]}]}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[ClassArr](js))
  }

  test("Multidimensional arrays must work") {
    describe("+++ Complex Types +++")
    val inst = MultiArr(null, Array(Array( Array(1L,2L), Array(3L,4L) ), Array(Array(5L,6L), Array(7L,8L)) ), 
      Array(Array(BigInt(12),BigInt(13)), Array(BigInt(14),BigInt(15))))
    val js = sj.render(inst)
    assertEquals(
      """{"a0":null,"a1":[[[1,2],[3,4]],[[5,6],[7,8]]],"a2":[[12,13],[14,15]]}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[MultiArr](js))
  }


  test("BigDecimal must work") {
    describe("----------------------\n:  Java Array Tests  :\n----------------------", Console.BLUE)
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