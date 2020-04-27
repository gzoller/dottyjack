package co.blocke.scalajack
package json.plainclass

import co.blocke.dottyjack.model.ClassNameHintModifier
import co.blocke.dotty_reflection._
import TestUtil._
import munit._
import munit.internal.console
import co.blocke.dottyjack.json.JSON


class Misc() extends FunSuite:

  val sj = co.blocke.dottyjack.DottyJack()

  /*
  test("Read/write null into object") {
    describe("------------------------\n:  Misc Tests (Plain)  :\n------------------------", Console.BLUE)

    assert(null == sj.read[PlayerMix]("null".asInstanceOf[JSON]) )
    assert("null".asInstanceOf[JSON] == sj.render[PlayerMix](null) )
  }
  */

  test("Handles type members with modifier") {
    val prependHintMod = ClassNameHintModifier(
      (hint: String) => "co.blocke.scalajack.json.plainclass." + hint,
      (cname: String) => cname.split('.').last
    )
    val sj2 = co.blocke.dottyjack.DottyJack().withTypeValueModifier(prependHintMod)
    val js = """{"flower":"Flower","rose":{"thing":5,"other":6}}""".asInstanceOf[JSON]
    val inst = sj2.read[WrapTrait[TraitBase]](js)
    assert(inst.rose.isInstanceOf[Flower])
    assertEquals(sj2.render(inst), js)
  }

  /*
  test("Fails if no hint for type member") {
    val js = """{"rose":{"thing":5,"other":6}}"""
    val msg =
      """Did not find required type member(s): flower
      |{"rose":{"thing":5,"other":6}}
      |^""".stripMargin
    the[ScalaJackError] thrownBy sj.read[WrapTrait[TraitBase]](js) should have message msg
  }

  test("Must accept missing default constructor values") {
    val js = """{"foobar":3, "quatro":4, "dontForget":1}"""
    val inst = sj.read[InheritSimpleBase](js)
    inst.one should be("blather")
  }

  test("Must accept missing optional constructor values") {
    val js = """{}"""
    val inst = sj.read[OptConst](js)
    inst.a should be(None)
    inst.b should be(Some(3))
  }

  test("Must ignore unneeded type members") {
    val inst = new UnneededType[String]()
    inst.a = 9
    sj.render(inst) should be("""{"a":9}""")
  }

  test("Must require Java classes to have an empty constructor") {
    val inst = new Unsupported("Foo")
    the[IllegalStateException] thrownBy sj.render(inst) should have message """ScalaJack does not support Java classes with a non-empty constructor."""
  }

  test("Must handle MapName on Java setter") {
    val js = """{"dos":9}"""
    val inst = sj.read[OnSetter](js)
    inst.getTwo should be(9)
  }
*/