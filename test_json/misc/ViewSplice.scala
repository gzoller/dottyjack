package co.blocke.scalajack
package json.misc

import co.blocke.dotty_reflection._
import TestUtil._
import munit._
import munit.internal.console
import co.blocke.dottyjack.json.JSON


class ViewSplice() extends FunSuite:

  val sj = co.blocke.dottyjack.DottyJack()
  
  test("Must process view") {
    val master = Master(
      "Greg",
      List("a", "b"),
      List(Encapsulated("x", bar = false), Encapsulated("y", bar = true)),
      Encapsulated("Nest!", bar = true),
      Some("wow"),
      Map("hey" -> 17, "you" -> 21),
      flipflop = true,
      99123986123L,
      Num.C,
      46
    )
    assertEquals(
      sj.view[View2](master),
      View2("Greg", flipflop = true, Map("hey" -> 17, "you" -> 21))
    )
  }


  test("Must process empty collections in view") {
    val master = Master(
      "Greg",
      List(),
      List(Encapsulated("x", bar = false), Encapsulated("y", bar = true)),
      Encapsulated("Nest!", bar = true),
      Some("wow"),
      Map("hey" -> 17, "you" -> 21),
      flipflop = true,
      99123986123L,
      Num.C,
      46
    )
    assertEquals(
      sj.view[Empty](master),
      Empty("Greg", List.empty[String])
    )
  }

  test("Must enforce View object as a case class") {
    val master = Master(
      "Greg",
      List("a", "b"),
      List(Encapsulated("x", bar = false), Encapsulated("y", bar = true)),
      Encapsulated("Nest!", bar = true),
      Some("wow"),
      Map("hey" -> 17, "you" -> 21),
      flipflop = true,
      99123986123L,
      Num.C,
      46
    )
    interceptMessage[co.blocke.dottyjack.ScalaJackError]("""Output of view() must be a case class. scala.Int is not a case class."""){
      sj.view[Int](master)
    }
  }

  test("Must enforce required constructor fields") {
    val master = Master(
      "Greg",
      List("a", "b"),
      List(Encapsulated("x", bar = false), Encapsulated("y", bar = true)),
      Encapsulated("Nest!", bar = true),
      Some("wow"),
      Map("hey" -> 17, "you" -> 21),
      flipflop = true,
      99123986123L,
      Num.C,
      46
    )
    val msg =
      """View master object co.blocke.scalajack.json.misc.Master is missing field bar required to build view object co.blocke.scalajack.json.misc.Encapsulated"""
    interceptMessage[co.blocke.dottyjack.ScalaJackError](msg){
      sj.view[Encapsulated](master)
    }
  }

  test("Must spliceInto") {
    val master = Master(
      "Greg",
      List("a", "b"),
      List(Encapsulated("x", bar = false), Encapsulated("y", bar = true)),
      Encapsulated("Nest!", bar = true),
      Some("wow"),
      Map("hey" -> 17, "you" -> 21),
      flipflop = true,
      99123986123L,
      Num.C,
      46
    )
    val x = sj.view[View1](master)
    val y: Master = sj.spliceInto(x.copy(name = "Fred", big = 2L), master)
    assertEquals(y, master.copy(name = "Fred", big = 2L))
  }

  test("Must enforce spliceInto target is a case class") {
    val master = Master(
      "Greg",
      List("a", "b"),
      List(Encapsulated("x", bar = false), Encapsulated("y", bar = true)),
      Encapsulated("Nest!", bar = true),
      Some("wow"),
      Map("hey" -> 17, "you" -> 21),
      flipflop = true,
      99123986123L,
      Num.C,
      46
    )
    val x = sj.view[View1](master)
    interceptMessage[co.blocke.dottyjack.ScalaJackError]("""Output of spliceInto() must be a case class.  java.lang.String is not a case class."""){
      sj.spliceInto(
        x.copy(name = "Fred", big = 2L),
        "some non-case-class"
      )
    }
  }

  test("Must spliceInto with empty collection from view") {
    val master = Master(
      "Greg",
      List("a", "b"),
      List(Encapsulated("x", bar = false), Encapsulated("y", bar = true)),
      Encapsulated("Nest!", bar = true),
      Some("wow"),
      Map("hey" -> 17, "you" -> 21),
      flipflop = true,
      99123986123L,
      Num.C,
      46
    )
    val x = Empty("Greg", List.empty[String])
    val y: Master = sj.spliceInto(x, master)
    assertEquals(y, master.copy(stuff = List.empty[String]))
  }

  test("Splicing in an object where not all fields match must splice in the compatible fields") {
    val master = Master(
      "Greg",
      List("a", "b"),
      List(Encapsulated("x", bar = false), Encapsulated("y", bar = true)),
      Encapsulated("Nest!", bar = true),
      Some("wow"),
      Map("hey" -> 17, "you" -> 21),
      flipflop = true,
      99123986123L,
      Num.C,
      46
    )
    val x = Partial("Mike", 50)
    val y: Master = sj.spliceInto(x, master)
    assertEquals(y, master.copy(name = "Mike"))
  }

  test("Splicing in an incompatible object shall nave no impact on the master") {
    val master = Master(
      "Greg",
      List("a", "b"),
      List(Encapsulated("x", bar = false), Encapsulated("y", bar = true)),
      Encapsulated("Nest!", bar = true),
      Some("wow"),
      Map("hey" -> 17, "you" -> 21),
      flipflop = true,
      99123986123L,
      Num.C,
      46
    )
    val x = NoMatch(bogus = true, 25)
    val y: Master = sj.spliceInto(x, master)
    assertEquals(y, master)
  }