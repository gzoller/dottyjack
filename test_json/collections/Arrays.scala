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
    val i2 = sj.read[BigDecimalArr](js)
    assert(inst.a1.sameElements(i2.a1))
    assert(inst.a2.sameElements(i2.a2))
  }

  test("BigInt must work") {
    val inst = BigIntArr(null, Array(BigInt(123),BigInt(78)))
    val js = sj.render(inst)
    assertEquals(
      """{"a1":null,"a2":[123,78]}""".asInstanceOf[JSON],
      js
    )
    val i2 = sj.read[BigIntArr](js)
    assert(inst.a1.sameElements(i2.a1))
    assert(inst.a2.sameElements(i2.a2))
  }

  test("Boolean must work") {
    val inst = BooleanArr(null, Array(true,false))
    val js = sj.render(inst)
    assertEquals(
      """{"a1":null,"a2":[true,false]}""".asInstanceOf[JSON],
      js
    )
    val i2 = sj.read[BooleanArr](js)
    assert(inst.a1.sameElements(i2.a1))
    assert(inst.a2.sameElements(i2.a2))
  }

  // No Array[Byte] because that's seen as binary data and treated differently
  test("Char must work") {
    val inst = CharArr(null, Array('a','b','c'))
    val js = sj.render(inst)
    assertEquals(
      """{"a1":null,"a2":["a","b","c"]}""".asInstanceOf[JSON],
      js
    )
    val i2 = sj.read[CharArr](js)
    assert(inst.a1.sameElements(i2.a1))
    assert(inst.a2.sameElements(i2.a2))
  }

  test("Double must work") {
    val inst = DoubleArr(null, Array(12.34,56.78))
    val js = sj.render(inst)
    assertEquals(
      """{"a1":null,"a2":[12.34,56.78]}""".asInstanceOf[JSON],
      js
    )
    val i2 = sj.read[DoubleArr](js)
    assert(inst.a1.sameElements(i2.a1))
    assert(inst.a2.sameElements(i2.a2))
  }

  test("Float must work") {
    val inst = FloatArr(null, Array(12.34F,56.78F))
    val js = sj.render(inst)
    assertEquals(
      """{"a1":null,"a2":[12.34,56.78]}""".asInstanceOf[JSON],
      js
    )
    val i2 = sj.read[FloatArr](js)
    assert(inst.a1.sameElements(i2.a1))
    assert(inst.a2.sameElements(i2.a2))
  }

  test("Int must work") {
    val inst = IntArr(null, Array(1,2,3))
    val js = sj.render(inst)
    assertEquals(
      """{"a1":null,"a2":[1,2,3]}""".asInstanceOf[JSON],
      js
    )
    val i2 = sj.read[IntArr](js)
    assert(inst.a1.sameElements(i2.a1))
    assert(inst.a2.sameElements(i2.a2))
  }

  test("Long must work") {
    val inst = LongArr(null, Array(1L,2L,3L))
    val js = sj.render(inst)
    assertEquals(
      """{"a1":null,"a2":[1,2,3]}""".asInstanceOf[JSON],
      js
    )
    val i2 = sj.read[LongArr](js)
    assert(inst.a1.sameElements(i2.a1))
    assert(inst.a2.sameElements(i2.a2))
  }

  test("Short must work") {
    val inst = ShortArr(null, Array(1.toShort,2.toShort,3.toShort))
    val js = sj.render(inst)
    assertEquals(
      """{"a1":null,"a2":[1,2,3]}""".asInstanceOf[JSON],
      js
    )
    val i2 = sj.read[ShortArr](js)
    assert(inst.a1.sameElements(i2.a1))
    assert(inst.a2.sameElements(i2.a2))
  }

  test("String must work") {
    val inst = StringArr(null, Array("a","b","c"))
    val js = sj.render(inst)
    assertEquals(
      """{"a1":null,"a2":["a","b","c"]}""".asInstanceOf[JSON],
      js
    )
    val i2 = sj.read[StringArr](js)
    assert(inst.a1.sameElements(i2.a1))
    assert(inst.a2.sameElements(i2.a2))
  }

  test("Lists must work") {
    describe("+++ Collection Types +++")
    val inst = ListArr(Array( List(1,2,3), List(4,5,6) ))
    val js = sj.render(inst)
    assertEquals(
      """{"a1":[[1,2,3],[4,5,6]]}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[ListArr](js))

    // Try another Seq variant
    val inst2 = SetArr(Array( Set(1,2,3), Set(4,5,6) ))
    val js2 = sj.render(inst2)
    assertEquals(
      """{"a1":[[1,2,3],[4,5,6]]}""".asInstanceOf[JSON],
      js2
    )
    assertEquals(inst2, sj.read[SetArr](js2))
  }

  test("Maps must work") {
    val inst = MapArr(Array( Map("a"->1,"b"->2), Map("c"->3,"d"->4) ))
    val js = sj.render(inst)
    assertEquals(
      """{"a1":[{"a":1,"b":2},{"c":3,"d":4}]}""".asInstanceOf[JSON],
      js
    )
    assertEquals(inst, sj.read[MapArr](js))
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


  test("Java primitives must work") {
    describe("----------------------\n:  Java Array Tests  :\n----------------------", Console.BLUE)
    describe("+++ Primitive Types +++")
    val inst = JavaArray()
    inst.setBigDecs(Array(java.math.BigDecimal.valueOf(123.4),java.math.BigDecimal.valueOf(456.7)))
    inst.setBigInts(Array(java.math.BigInteger.valueOf(123),java.math.BigInteger.valueOf(456)))
    inst.setBooleans(Array(java.lang.Boolean.valueOf(true),java.lang.Boolean.valueOf("false")))
    inst.setBytes(Array(java.lang.Byte.valueOf(24.toByte),java.lang.Byte.valueOf(12.toByte)))
    inst.setCharacters(Array(java.lang.Character.valueOf('a'),java.lang.Character.valueOf('z')))
    inst.setDoubles(Array(java.lang.Double.valueOf(12.34),java.lang.Double.valueOf(-56.78)).asInstanceOf[Array[java.lang.Double]])
    inst.setFloats(Array(java.lang.Float.valueOf(12.34F),java.lang.Float.valueOf(-56.78F)))
    inst.setIntegers(Array(java.lang.Integer.valueOf(1),java.lang.Integer.valueOf(2)))
    inst.setLongs(Array(java.lang.Long.valueOf(1),java.lang.Long.valueOf(2)))
    inst.setShorts(Array(java.lang.Short.valueOf(1.toShort),java.lang.Short.valueOf(2.toShort)))
    inst.setMulti(Array(Array(java.math.BigInteger.valueOf(123),java.math.BigInteger.valueOf(456)), 
      Array(java.math.BigInteger.valueOf(543),java.math.BigInteger.valueOf(222))))
    val js = sj.render(inst)
    assertEquals(
      """{"bigDecs":[123.4,456.7],"bigInts":[123,456],"booleans":[true,false],"bytes":[24,12],"characters":["a","z"],"doubles":[12.34,-56.78],"floats":[12.34,-56.78],"integers":[1,2],"longs":[1,2],"multi":[[123,456],[543,222]],"shorts":[1,2]}""".asInstanceOf[JSON],
      js)
    val readIn = sj.read[JavaArray](js)
    assertEquals(inst.getBigDecs, readIn.getBigDecs)
    assertEquals(inst.getBigInts, readIn.getBigInts)
    assertEquals(inst.getBooleans, readIn.getBooleans)
    assertEquals(inst.getBytes, readIn.getBytes)
    assertEquals(inst.getCharacters, readIn.getCharacters)
    assertEquals(inst.getDoubles, readIn.getDoubles)
    assertEquals(inst.getFloats, readIn.getFloats)
    assertEquals(inst.getIntegers, readIn.getIntegers)
    assertEquals(inst.getLongs, readIn.getLongs)
    assertEquals(inst.getShorts, readIn.getShorts)
    assertEquals(inst.getMulti, readIn.getMulti)
  }
