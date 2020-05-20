package co.blocke.dottyjack

import co.blocke.dotty_reflection._
import co.blocke.dotty_reflection.info._
import org.apache.commons.codec.binary.Base64

import scala.collection.mutable
import scala.jdk.CollectionConverters._


object Food extends Enumeration {
  val Seeds, Meat, Pellets, Veggies = Value
}

case class Foom( a: Month )

case class Holder[T](a: Map[T,T])

enum Month {
  case Jan, Feb, Mar
}

object Main {

  def main(args: Array[String]): Unit = 

    val sj = DottyJack().enumsAsInts()

    val m1 = Map(Food.Meat -> Food.Veggies)
    val m2 = Map(Food.Seeds -> Food.Pellets)
    val inst = Map(Map(m1 -> m2) -> Map(m2 -> m1))
    val js = sj.render(inst)
    println(js)
    println(sj.read[Map[Map[Map[Food.Value,Food.Value], Map[Food.Value,Food.Value]],Map[Map[Food.Value,Food.Value], Map[Food.Value,Food.Value]]]](js))

    println("\n------\n")

    val h1 = Holder[Option[Food.Value]]( Map(Some(Food.Meat) -> Some(Food.Veggies)))
    val j1 = sj.render(h1)
    println(j1)
    println(sj.read[Holder[Option[Food.Value]]](j1))

    println("\n------\n")

    val h2 = Holder[Int|Boolean](Map(false->9))
    val j2 = sj.render(h2)
    println(j2)
    val x = sj.read[Holder[Int|Boolean]](j2)
    println(x)
    println(x.a.head._1.getClass)

  def constructors(clazz: Class[_]): String = 
    s"=== Constructors: ${clazz.getName} ===\n   " + clazz.getConstructors.toList.mkString("\n   ")
  def methods(clazz: Class[_]): String = 
    s"=== Methods: ${clazz.getName} ===\n   " + clazz.getMethods.toList.mkString("\n   ")
  def fields(clazz: Class[_]): String = 
    s"=== Fields: ${clazz.getName} ===\n   " + clazz.getFields.toList.mkString("\n   ")
  def stack(clazz: Class[_]): String = 
    s"=== Superclass: ${clazz.getName} ===\n   " + clazz.getSuperclass() + "\n" +
    s"=== Interfaces: ${clazz.getName} ===\n   " + clazz.getInterfaces.toList.mkString("\n   ")
  
}