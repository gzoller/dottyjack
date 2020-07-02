package co.blocke.scalajack
package json.misc

import co.blocke.dotty_reflection._
import TestUtil._
import munit._
import munit.internal.console
import co.blocke.dottyjack.json.JSON

case class EmptyClass()

class Skipping() extends FunSuite:

  val sj = co.blocke.dottyjack.DottyJack()
  
  val examples = Map(
    "string" -> """"haha"""",
    "number" -> """1""",
    "null" -> """null""",
    "boolean" -> """true""",
    "list of strings" -> """["a", "b", "c"]""",
    "list of objects" -> """[{"a":1},{"b":2},{"c":3}]"""
  )

  test("Should skip") {
    describe("--------------\n:  Skipping  :\n--------------", Console.BLUE)

    val json =
      """
        |{
        |  "top": {
        |    "middle": [
        |      {
        |        "a": "1"
        |      },
        |      {
        |        "b": "2"
        |      },
        |      {
        |        "c": "3"
        |      }
        |    ]
        |  }
        |}
        |
      """.stripMargin.asInstanceOf[JSON]
    assertEquals(EmptyClass(), sj.read[EmptyClass](json))
  }

  for ((description, json) <- examples) {
    test(s"Should skip a $description field value") {
      assertEquals(EmptyClass(),
        sj.read[EmptyClass](
          s"""{"fieldThatDoesNotExistAndThusRequiresSkippingThereforeQED":$json}""".asInstanceOf[JSON]
        )
      )
    }
  }

  test("Skipping string with embedded special chars works") {
    val js =
      """{"name":"Fido \"The Beast\"","_hint":"co.blocke.scalajack.json.misc.Dog","kind":15}""".asInstanceOf[JSON]
    assert(Dog("""Fido "The Beast"""", 15) == sj.read[Pet](js))
  }

  test("Skip over nested lists") {
    val js =
      """{"name":"Fido \"The Beast\"","_hint":"co.blocke.scalajack.json.misc.Dog","notneeded":[[1,2],[3,4]],"kind":15}""".asInstanceOf[JSON]
    assert(Dog("""Fido "The Beast"""", 15) == sj.read[Pet](js))
  }
