package co.blocke.scalajack
package json
package plainclass

import co.blocke.dottyjack.model.ClassNameHintModifier
import co.blocke.dotty_reflection._
import TestUtil._
import munit._
import munit.internal.console
import co.blocke.dottyjack.json.JSON
import JsonMatcher._


class Inheritance() extends FunSuite:

  val sj = co.blocke.dottyjack.DottyJack()

  test("Simple class inheritance must work (all fields present)") {
    describe(
      "-------------------------------------\n:  Inheritance Tests (Plain Class)  :\n-------------------------------------", Console.BLUE
    )
    describe("Scala Plain")

    println(Reflector.reflectOn[InheritSimpleChild])

    val js =
      """{"uno":"foo","foobar":99,"dontForget":12,"three":false,"quatro":12.34,"foo":25,"extra":"bar"}""".asInstanceOf[JSON]
    val simple = sj.read[InheritSimpleChild](js)
    assertEquals(simple.one, "foo")
    assertEquals(simple.extra, "bar")
    assertEquals(simple.foo, 25)
    assertEquals(simple.two, 99)
    assertEquals(simple.three, false)
    assertEquals(simple.four, 12.34)
    // Need this matching because JSON order is often different
    JsonMatcher.jsonMatches(js, sj.render(simple))
  }

  /*
  test("MapName, and Ignore annotations must be inherited properly") {
    val adapter = sj.taCache
      .typeAdapterOf[InheritSimpleChild]
      .asInstanceOf[typeadapter.PlainClassTypeAdapter[_]]
    adapter.dbKeys.map(f => (f.name, f.dbKeyIndex)) should be(
      List(
        ("uno", Some(0)),
        ("foobar", Some(1)),
        ("quatro", Some(2)),
        ("foo", Some(99))
      )
    )
    val inst = new InheritSimpleChild("thing1", "thing2")
    val js = sj.render(inst)
    // Need this matching because JSON order is often different
    parseJValue(js) should matchJson(
      parseJValue(
        """{"extra":"thing1","uno":"thing2","foo":39,"dontForget":9,"quatro":0.1,"three":true,"foobar":5}"""
      )
    )
  }

  test("Optional annotation must be inherited properly") {
    val js =
      """{"extra":"bar","foo":25,"uno":"something","dontForget":12,"quatro":12.34,"foobar":99}"""
    val inst = sj.read[InheritSimpleChild](js)
    inst.one should be("something")
    inst.three should be(true)
  }

  test("With type parameter") {
    val js = """{"thing":5, "item": 15, "cosa": 99}"""
    val inst = sj.read[ParamChild[Int]](js)
    inst.thing should be(5)
    inst.item should be(15)
    inst.cosa should be(99)
    sj.render(inst) should be("""{"thing":5,"cosa":99,"item":15}""")
  }

  test("With type member (as part of a trait)") {
    val inst = new WrapTrait[Flower]()
    val flower = new Flower(5, 6)
    inst.rose = flower
    val js = sj.render(inst)
    js should be(
      """{"flower":"co.blocke.scalajack.json.primitives.plain.Flower","rose":{"thing":5,"other":6}}"""
    )
    val inst2 = sj.read[WrapTrait[TraitBase]](js)
    (inst2.rose.thing == flower.thing && inst2.rose.other == flower.other) should be(
      true
    )
  }

  test("Must catch missing/required var") {
    describe("Scala Plain Negative") 

    val js =
      """{"extra":"bar","foo":25,"dontForget":12,"uno":"something","quatro":12.34}"""
    val msg = """Class InheritSimpleChild missing required fields: foobar
              |...,"dontForget":12,"uno":"something","quatro":12.34}
              |----------------------------------------------------^""".stripMargin
    the[ScalaJackError] thrownBy sj.read[InheritSimpleChild](js) should have message msg
  }

  test("Must catch missing/required constructor field (with newline)") {
    val js =
      """{"extra":"bar","foo":25,"dontForget":12,"quatro"
      |:12.34,"foobar":99}""".stripMargin
    val msg =
      """Class InheritSimpleChild missing required constructor fields: uno
              |...o":25,"dontForget":12,"quatro"~:12.34,"foobar":99}
              |----------------------------------------------------^""".stripMargin
    the[ScalaJackError] thrownBy sj.read[InheritSimpleChild](js) should have message msg
  }

  test("Must catch missing/required getter/setter field") {
    val js =
      """{"extra":"bar","foo":25,"uno":"something","quatro":12.34,"foobar":99}"""
    val msg =
      """Class InheritSimpleChild missing required fields: dontForget
              |...":25,"uno":"something","quatro":12.34,"foobar":99}
              |----------------------------------------------------^""".stripMargin
    the[ScalaJackError] thrownBy sj.read[InheritSimpleChild](js) should have message msg
  }
  test("Must fail non-val constructor field") {
    val f = new Fail4(1, 2)
    the[java.lang.IllegalStateException] thrownBy sj.render(f) should have message """ScalaJack doesn't support non-val constructor fields (they can't be read by reflection)"""
  }


  test(
    "Simple class inheritance must work (all fields present) including MapName and Ignore"
  ) {
    describe("Java Plain") 

    val js = """{"three":3,"dos":1}"""
    val simple = sj.read[JavaSimpleChild](js)
    simple.getTwo should be(1)
    simple.getThree should be(3)
    simple.getBogus should be(-1)
    sj.render(simple) should be(js)
  }

  test("Optional annotation must be inherited properly") {
    val js = """{"dos":1}"""
    val simple = sj.read[JavaSimpleChild](js)
    simple.getTwo should be(1)
    simple.getThree should be(-10)
    sj.render(simple) should be("""{"three":-10,"dos":1}""")
  }
*/