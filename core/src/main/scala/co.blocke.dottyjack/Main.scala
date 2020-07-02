package co.blocke.dottyjack

import co.blocke.dotty_reflection._
import co.blocke.dotty_reflection.info._
import org.apache.commons.codec.binary.Base64

import scala.collection.mutable
import scala.jdk.CollectionConverters._
import co.blocke.dottyjack.json.JSON


trait Thing[A, B] { val a: A; val b: B }
case class AThing[Y, X](a: X, b: Y) extends Thing[X, Y]

object Main {

  def main(args: Array[String]): Unit = 

    // val sj = DottyJack()
    val sj = co.blocke.dottyjack.DottyJack()

    // println(RType.of[Map[Map[Map[Thing[String, Int], Thing[String, Int]], Map[Thing[String, Int], Thing[String, Int]]], Map[Map[Thing[String, Int], Thing[String, Int]], Map[Thing[String, Int], Thing[String, Int]]]]])
    val m1: Map[Thing[String, Int], Thing[String, Int]] =
      Map(AThing("one", 1) -> AThing("two", 2))
    val m2: Map[Thing[String, Int], Thing[String, Int]] =
      Map(AThing("four", 4) -> AThing("three", 3))
    val inst = Map(Map(m1 -> m2) -> Map(m2 -> m1))
    val js = sj.render(inst)
    println(js)
    println(sj.read[Map[Map[Map[Thing[String, Int], Thing[String, Int]], Map[Thing[String, Int], Thing[String, Int]]], Map[Map[Thing[String, Int], Thing[String, Int]], Map[Thing[String, Int], Thing[String, Int]]]]](js))


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