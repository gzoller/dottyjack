package co.blocke.scalajack
package json.misc

import co.blocke.dotty_reflection._
import TestUtil._
import munit._
import munit.internal.console
import co.blocke.dottyjack.json.JSON


class DefaultValues() extends FunSuite:

  val sj = co.blocke.dottyjack.DottyJack()
  
  test("Default values are found - not confused by optional values") {
    describe("-------------------------\n:  Default Value Tests  :\n-------------------------", Console.BLUE)
    val inst = SimpleHasDefaults("Me")
    val js = sj.render(inst)
    assertEquals("""{"name":"Me","age":5}""".asInstanceOf[JSON], js)
    assertEquals(inst, sj.read[SimpleHasDefaults](js))
    val missing = """{"name":"Me"}""".asInstanceOf[JSON]
    assertEquals(inst, sj.read[SimpleHasDefaults](missing))
  }

  test("Traits with default values handled") {
    val inst = HasDefaults("Me", None)
    val js = sj.render(inst)
    assertEquals(
      """{"name":"Me","pet":{"_hint":"co.blocke.scalajack.json.misc.Dog","name":"Fido","kind":true}}""".asInstanceOf[JSON], js)
    assertEquals(inst, sj.read[HasDefaults](js))
    val missing = """{"name":"Me"}""".asInstanceOf[JSON]
    assertEquals(inst, sj.read[HasDefaults](missing))
  }

  test("Marshals default optional value (before assuming None)") {
    val js = """{"name": "Harry"}""".asInstanceOf[JSON]
    assertEquals(DefaultOpt("Harry"), sj.read[DefaultOpt](js))
  }

  test("Optional default value and null") {
    val js = """{"name": "Harry", "age":null}""".asInstanceOf[JSON]
    val js2 = """{"name": "Harry"}""".asInstanceOf[JSON]
    assertEquals(DefaultOpt("Harry", null), sj.read[DefaultOpt](js))
    assertEquals(DefaultOpt("Harry", Some(19)), sj.read[DefaultOpt](js2))
  }

  test("Fails if no default is found for a given field") {
    val js = """{"age":null}""".asInstanceOf[JSON]
    val msg = """Class co.blocke.scalajack.json.misc.DefaultOpt missing required fields: name
              |{"age":null}
              |-----------^""".stripMargin
    interceptMessage[co.blocke.dottyjack.ScalaJackError](msg){
      sj.read[DefaultOpt](js)
    }
  }