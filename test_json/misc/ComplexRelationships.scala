package co.blocke.scalajack
package json
package misc

import co.blocke.dotty_reflection._
import TestUtil._
import munit._
import munit.internal.console
import co.blocke.dottyjack.json.JSON


class ComplexRelationships() extends FunSuite:

  val sj = co.blocke.dottyjack.DottyJack()

  test("Child class declares a type parameter not provided by the parent (trait)") {
    describe(
      "---------------------------------\n:  Complex Relationships Tests  :\n---------------------------------", Console.BLUE
    )

    val inst: Parent[Int, Boolean] = Child(1, true, "here")
    val js                         = sj.render(inst)
    assertEquals(
      """{"_hint":"co.blocke.scalajack.json.misc.Child","a":1,"b":true,"c":"here"}""".asInstanceOf[JSON], js)
    assertEquals(inst, sj.read[Parent[Int, Boolean]](js))
  }

  test("Parameterized type implements non-parameterized trait") {
    val inst: Pet = Dog("Fido", Dog("Larry", true))
    val js        = sj.render(inst)
    assertEquals(
      """{"_hint":"co.blocke.scalajack.json.misc.Dog","name":"Fido","kind":{"_hint":"co.blocke.scalajack.json.misc.Dog","name":"Larry","kind":true}}""".asInstanceOf[JSON], js)
    assert(classOf[java.lang.Boolean] == {
      inst.asInstanceOf[Dog[_]].kind.asInstanceOf[Dog[_]].kind.getClass
    })
    assert(classOf[java.lang.Boolean] == {
      val found = sj.read[Pet](js)
      found.asInstanceOf[Dog[_]].kind.asInstanceOf[Dog[_]].kind.getClass
    })
  }

  test(
    "Parameterized type implements non-parameterized trait (using Long for parameter type"
  ) {
    val inst: Pet = Dog("Fido", Dog("Larry", 15L))
    val js        = sj.render(inst)
    assertEquals(
      """{"_hint":"co.blocke.scalajack.json.misc.Dog","name":"Fido","kind":{"_hint":"co.blocke.scalajack.json.misc.Dog","name":"Larry","kind":15}}""".asInstanceOf[JSON], js)
    assert(classOf[java.lang.Long] == {
      inst.asInstanceOf[Dog[_]].kind.asInstanceOf[Dog[_]].kind.getClass
    })
    // Loss of fidelity on read: Long -> Integer
    assert(classOf[java.lang.Integer] == {
      val found = sj.read[Pet](js)
      found.asInstanceOf[Dog[_]].kind.asInstanceOf[Dog[_]].kind.getClass
    })
  }

  test(
    "Parameterized type implements non-parameterized trait (using enum for parameter type)"
  ) {
    val inst: Pet = Dog("Fido", Kind.Pug)
    val js        = sj.render(inst)
    assertEquals(
      """{"_hint":"co.blocke.scalajack.json.misc.Dog","name":"Fido","kind":"Pug"}""".asInstanceOf[JSON], js)
    assert(Kind.Pug.getClass == {
      inst.asInstanceOf[Dog[_]].kind.getClass
    })
    // Loss of fidelity on read:  Kind.Pug (Enumeration) -> String
    assert(classOf[String] == {
      val found = sj.read[Pet](js)
      found.asInstanceOf[Dog[_]].kind.getClass
    })
  }

  test("Parameterized type implements non-parameterized trait (with Map)") {
    val inst: Pet = Dog("Fido", Map("Larry" -> true))
    val js        = sj.render(inst)
    assertEquals(
      """{"_hint":"co.blocke.scalajack.json.misc.Dog","name":"Fido","kind":{"Larry":true}}""".asInstanceOf[JSON], js)
    assert((classOf[String], classOf[java.lang.Boolean]) == {
      val found = sj.read[Pet](js)
      val c: (Any, Any) =
        found
          .asInstanceOf[Dog[_]]
          .kind
          .asInstanceOf[Map[_, _]]
          .head
      (c._1.getClass, c._2.getClass)
    })
  }
