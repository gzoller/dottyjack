package co.blocke.dottyjack

import co.blocke.dotty_reflection._
import co.blocke.dotty_reflection.infos._

// Scala 2.x style Enumeration
object WeekDay extends Enumeration {
  type WeekDay = Value
  val Monday = Value(1)
  val Tuesday = Value(2)
  val Wednesday = Value(3)
  val Thursday = Value(4)
  val Friday = Value(5)
  val Saturday = Value(6)
  val Sunday = Value(-3)
}
import WeekDay._

// Scala 3 Enum
enum Month {
  case Jan, Feb, Mar
}
import Month._

object Size extends Enumeration {
  type Size = Value
  val Small, Medium, Large = Value
}
import Size._
case class SampleEnum(e5: Size.Value)


case class Holder( day: WeekDay )


object Main {

  def main(args: Array[String]): Unit = {

    val dj = DottyJack()

    // val js = dj.render(Holder(Tuesday))
    val js = dj.render(SampleEnum(Large))
    println(js)
    println(dj.read[SampleEnum](js))

    // named: TypeRef(TermRef(TermRef(ThisType(TypeRef(NoPrefix,module class blocke)),module dottyjack),WeekDay),WeekDay)
    // ClassSym: class Value
    // Find class: co.blocke.dottyjack.WeekDay

    // named: TypeRef(TermRef(TermRef(ThisType(TypeRef(NoPrefix,module class blocke)),module dottyjack),Size),Value)
    // ClassSym: class Value
    // Find class:

  }


  def methods(clazz: Class[_]): String = 
    s"=== Methods: ${clazz.getName} ===\n   " + clazz.getMethods.toList.mkString("\n   ")
  def fields(clazz: Class[_]): String = 
    s"=== Fields: ${clazz.getName} ===\n   " + clazz.getFields.toList.mkString("\n   ")
  def stack(clazz: Class[_]): String = 
    s"=== Superclass: ${clazz.getName} ===\n   " + clazz.getSuperclass() + "\n" +
    s"=== Interfaces: ${clazz.getName} ===\n   " + clazz.getInterfaces.toList.mkString("\n   ")
  
}