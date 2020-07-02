package co.blocke.scalajack
package json.misc

import co.blocke.dotty_reflection._
import TestUtil._
import munit._
import munit.internal.console
import co.blocke.dottyjack.json.JSON

class SelfReference() extends FunSuite:

  val sj = co.blocke.dottyjack.DottyJack()

  test("Null self-ref") {
    describe("--------------------------\n:  Self Reference Tests  :\n--------------------------", Console.BLUE)
    describe("Render Basic self-reference")

    val data = HooLoo("Greg", null)
    val js = sj.render(data)
    assertEquals(js, """{"name":"Greg","more":null}""".asInstanceOf[JSON])
    assertEquals(sj.read[HooLoo](js), data)
  }

  test("Non-null self-ref") {
    val d2 = HooLoo("Garth", null)
    val data = HooLoo("Greg", d2)
    val js = sj.render(data)
    assertEquals(js,"""{"name":"Greg","more":{"name":"Garth","more":null}}""".asInstanceOf[JSON])
    assertEquals(sj.read[HooLoo](js), data)
  }

  test("Collection of self-ref (null)") {
    describe("Render Collection of self-reference") 

    val data = HooLoo5("Greg", null)
    val js = sj.render(data)
    assertEquals(js,"""{"name":"Greg","more":null}""".asInstanceOf[JSON])
    assertEquals(sj.read[HooLoo5](js), data)
  }

  test("Collection of self-ref (non-null)") {
    val data =
      HooLoo5("Greg", List(HooLoo5("Garth", null), HooLoo5("Graham", null)))
    val js = sj.render(data)
    assertEquals(js,"""{"name":"Greg","more":[{"name":"Garth","more":null},{"name":"Graham","more":null}]}""".asInstanceOf[JSON])
    assertEquals(sj.read[HooLoo5](js), data)
  }

  test("Collection of self-ref (empty but non-null)") {
    val data = HooLoo5("Greg", List.empty[HooLoo5])
    val js = sj.render(data)
    assertEquals(js,"""{"name":"Greg","more":[]}""".asInstanceOf[JSON])
    assertEquals(sj.read[HooLoo5](js), data)
  }

  test("Collection of self-ref (containing a mix of null/non-null elements)") {
    val data = HooLoo5(
      "Greg",
      List(HooLoo5("Garth", null), null, HooLoo5("Graham", null))
    )
    val js = sj.render(data)
    assertEquals(js, """{"name":"Greg","more":[{"name":"Garth","more":null},null,{"name":"Graham","more":null}]}""".asInstanceOf[JSON])
    assertEquals(sj.read[HooLoo5](js), data)
  }

  test("Option of self-ref (some)") {
    val data = HooLoo4("Greg", Some(HooLoo4("Garth", null)))
    val js = sj.render(data)
    assertEquals(js, """{"name":"Greg","more":{"name":"Garth","more":null}}""".asInstanceOf[JSON])
    assertEquals(sj.read[HooLoo4](js), HooLoo4("Greg", Some(HooLoo4("Garth", null)))) // the null converts into None when read
  }

  test("Option of self-ref (none)") {
    val data = HooLoo4("Greg", None)
    val js = sj.render(data)
    assertEquals(js, """{"name":"Greg"}""".asInstanceOf[JSON])
    assertEquals(sj.read[HooLoo4](js), data)
  }

  test("Basic param self-ref (null)") {
    describe("Render parameterized self-ref")
    val data = HooLoo2("Greg", true, null)
    val js = sj.render(data)
    assertEquals(js, """{"name":"Greg","x":true,"more":null}""".asInstanceOf[JSON])
    assertEquals(sj.read[HooLoo2[Boolean]](js), data)
  }

  test("Basic param self-ref (non-null)") {
    val data = HooLoo2("Greg", true, HooLoo2("Garth", 32, null))
    val js = sj.render(data)
    assertEquals(js, """{"name":"Greg","x":true,"more":{"name":"Garth","x":32,"more":null}}""".asInstanceOf[JSON])
    assertEquals(sj.read[HooLoo2[Boolean]](js), data)
  }

  test("Full param self-ref (null)") {
    val data = HooLoo3("Greg", true, null)
    val js = sj.render(data)
    assertEquals(js, """{"name":"Greg","x":true,"more":null}""".asInstanceOf[JSON])
    assertEquals(sj.read[HooLoo3[Boolean]](js), data)
  }

  test("Full param self-ref (non-null)") {
    val data = HooLoo3("Greg", true, HooLoo3("Garth", false, null))
    val js = sj.render(data)
    assertEquals(js, """{"name":"Greg","x":true,"more":{"name":"Garth","x":false,"more":null}}""".asInstanceOf[JSON])
    assertEquals(sj.read[HooLoo3[Boolean]](js), data)
  }

  test("Collection of param self-ref (null)") {
    describe("Render Collection of param self-reference")

    val data = HooLoo6("Greg", "hey", null)
    val js = sj.render(data)
    assertEquals(js, """{"name":"Greg","x":"hey","more":null}""".asInstanceOf[JSON])
    assertEquals(sj.read[HooLoo6[String]](js), data)
  }

  test("Collection of param self-ref (non-null)") {
    val data = HooLoo6(
      "Greg",
      "zero",
      List(HooLoo6("Garth", "one", null), HooLoo6("Graham", "two", null))
    )
    val js = sj.render(data)
    assertEquals(js, """{"name":"Greg","x":"zero","more":[{"name":"Garth","x":"one","more":null},{"name":"Graham","x":"two","more":null}]}""".asInstanceOf[JSON])
    assertEquals(sj.read[HooLoo6[String]](js), data)
  }

  test("Collection of param self-ref (empty but non-null)") {
    val data = HooLoo6("Greg", "hey", List.empty[HooLoo6[String]])
    val js = sj.render(data)
    assertEquals(js, """{"name":"Greg","x":"hey","more":[]}""".asInstanceOf[JSON])
    assertEquals(sj.read[HooLoo6[String]](js), data)
  }