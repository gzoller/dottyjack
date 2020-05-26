package co.blocke.scalajack
package json
package misc

import co.blocke.dotty_reflection._
import TestUtil._
import munit._
import munit.internal.console
import co.blocke.dottyjack.json.JSON
import scala.util._
import scala.language.postfixOps

case class Foo(name: String, stuff: List[String])

class ThreadSafety() extends FunSuite:

  val sj = co.blocke.dottyjack.DottyJack()

  test("Should not crash when multiple threads access Analyzer (Scala 2.10.x reflection bug)") {
    describe("-------------------\n:  Thread Safety  :\n-------------------",Console.BLUE)

    import scala.concurrent.ExecutionContext.Implicits.global
    import scala.concurrent.duration._
    import scala.concurrent.{ Await, Future }

    val doit = (i:Int) =>
      Try {
        val js = sj.render(Foo("Greg", List("a", "b", "c")))
        sj.read[Foo](js)
      }.toOption.isDefined
    val z =
      List(Future(doit(1)), Future(doit(2)), Future(doit(3)), Future(doit(4)))
    val res =
      Await.result(Future.sequence(z), 12 seconds).reduce((a, b) => a && b)
    assertEquals(res, true)
  }
