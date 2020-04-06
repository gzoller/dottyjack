package co.blocke.scalajack
package json.misc

import co.blocke.dotty_reflection._
import scala.math.BigDecimal
import java.util.UUID
import TestUtil._
import munit._
import munit.internal.console
import co.blocke.dottyjack.json.JSON

class GrabBag() extends FunSuite:

  val sj = co.blocke.dottyjack.DottyJack()

  test("Can't find TypeAdapter for given type") {
    describe("-----------------------------\n:  Scala Grab-Bag of Tests  :\n-----------------------------", Console.BLUE)
    pending
    // This produces a JavaClassInfo...
    // val js = """{"hey":"you"}""".asInstanceOf[JSON]
    // println(Reflector.reflectOn[java.lang.Process])
    // val msg =
    //   "Unable to find a type adapter for Process (abstract class or a dependency of an abstract class)"
    // interceptMessage[co.blocke.dottyjack.ScalaJackError](msg){
    //   sj.read[java.lang.Process](js)
    // }
  }
