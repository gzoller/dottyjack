package co.blocke.dottyjack

import co.blocke.dotty_reflection._
import co.blocke.dotty_reflection.info._
import org.apache.commons.codec.binary.Base64

import scala.collection.mutable
import scala.jdk.CollectionConverters._
import co.blocke.dottyjack.json.JSON

sealed trait ContactPoint
case class EmailAddress(emailAddress: String) extends ContactPoint
case class PhoneNumber(phoneNumber: String) extends ContactPoint


object Main {

  def main(args: Array[String]): Unit = 

    val sj = DottyJack().enumsAsInts()

    val js = """{"emailAddress":"foom!"}""".asInstanceOf[JSON]
    println( sj.read[ContactPoint](js) )

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