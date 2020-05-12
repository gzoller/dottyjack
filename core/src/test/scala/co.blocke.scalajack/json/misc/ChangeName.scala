package co.blocke.scalajack
package json
package misc

import co.blocke.dotty_reflection._
import TestUtil._
import munit._
import munit.internal.console
import co.blocke.dottyjack.json.JSON


class ChangeName() extends FunSuite:

  val sj = co.blocke.dottyjack.DottyJack()

  test("Mapping name for case class fields works") {
    describe(
      "-----------------------\n:  Change Name Tests  :\n-----------------------", Console.BLUE
    )
    val js = """{"foo_bar":"thing","a_b":123,"count":2}""".asInstanceOf[JSON]
    val r = sj.read[MapFactor](js)
    assertEquals(r, MapFactor("thing", 123L, 2))
    assertEquals(sj.render(r),js)
  }

  test("Mapping name for non-case class fields works") {
    val js = """{"pilot":"Sam","count":2,"a_b":123,"foo_bar":"thing"}""".asInstanceOf[JSON]
    val mfp = new MapFactorPlain("Sam")
    mfp.fooBar = "thing"
    mfp.thingy = 123L
    mfp.count = 2
    assert(JsonMatcher.jsonMatches(sj.render(mfp),js))
    assert {
      val r = sj.read[MapFactorPlain](js)
      (r.fooBar == mfp.fooBar && r.thingy == mfp.thingy && r.count == mfp.count)
    }
  }
