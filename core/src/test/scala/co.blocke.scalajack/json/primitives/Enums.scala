package co.blocke.scalajack
package json.primitives

import co.blocke.dotty_reflection._
import scala.math.BigDecimal
import java.util.UUID
import TestUtil._
import munit._
import munit.internal.console
import co.blocke.dottyjack.json.JSON

class Enums() extends FunSuite:

  val sj = co.blocke.dottyjack.DottyJack()

  test("Enumeration (Scala 2.x) must work (not nullable)") {
    describe("-----------------\n:  Scala Enums  :\n-----------------", Console.BLUE)
    describe("+++ Positive Tests +++")
    import SizeWithType._
    val inst = SampleEnum(Size.Small, Size.Medium, Size.Large, null, Size.Medium, Little)
    val js = sj.render(inst)
    assertEquals(
      """{"e1":"Small","e2":"Medium","e3":"Large","e4":null,"e5":"Medium","e6":"Little"}""".asInstanceOf[JSON],
      js)
    // mutate e5 into an ordinal...
    val js2 = js.asInstanceOf[String].replaceAll(""""e5":"Medium"""", """"e5":1""").asInstanceOf[JSON]
    assertEquals(inst, sj.read[SampleEnum](js2))
  }

  test("Enum (Scala 3.x) must work (not nullable)") {
    pending
  }

  test("""Sealed trait "enums" must work""") {
    pending
  }

  test("""Case object "enums" must work""") {
    pending
  }

  test("Enumeration (Scala 2.x) must break") {
    describe("--- Negative Tests ---")
    val js =
      """{"e1":"Small","e2":"Bogus","e3":"Large","e4":null,"e5":"Medium","e6":"Little"}""".asInstanceOf[JSON]
    val msg =
      """No value found in enumeration co.blocke.scalajack.json.primitives.Size$ for Bogus
              |{"e1":"Small","e2":"Bogus","e3":"Large","e4":null,"e5":"Medium","e6":"Little"}
              |-------------------------^""".stripMargin
    interceptMessage[co.blocke.dottyjack.ScalaJackError](msg){
      sj.read[SampleEnum](js)
    }
    val js2 =
      """{"e1":"Small","e2":"Medium","e3":"Large","e4":null,"e5":9,"e6":"Little}""".asInstanceOf[JSON]
    val msg2 =
      """No value found in enumeration co.blocke.scalajack.json.primitives.Size$ for 9
               |...Small","e2":"Medium","e3":"Large","e4":null,"e5":9,"e6":"Little}
               |----------------------------------------------------^""".stripMargin
    interceptMessage[co.blocke.dottyjack.ScalaJackError](msg2){
      sj.read[SampleEnum](js2)
    }
    val js3 =
      """{"e1":"Small","e2":"Medium","e3":"Large","e4":null,"e5":false,"e6":"Little}""".asInstanceOf[JSON]
    val msg3 = """Expected a Number or String here
               |...Small","e2":"Medium","e3":"Large","e4":null,"e5":false,"e6":"Little}
               |----------------------------------------------------^""".stripMargin
    interceptMessage[co.blocke.dottyjack.ScalaJackError](msg3){
      sj.read[SampleEnum](js3)
    }
  }

  test("Enum (Scala 3.x) must break") {
    pending
  }

  //--------------------------------------

  test("Java Enumeration (not nullable) must work") {
    describe("----------------------\n:  Java Enumeration  :\n----------------------", Console.BLUE)
    describe("+++ Positive Tests +++")
    pending
  }  

  test("Java Enumeration (not nullable) must fail") {
    describe("--- Negative Tests ---")
    pending
  }
