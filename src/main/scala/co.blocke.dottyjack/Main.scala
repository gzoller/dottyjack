package co.blocke.dottyjack

import co.blocke.dotty_reflection._
import co.blocke.dotty_reflection.infos._

case class Boom(x:Int)
case class Foom( stuff: Array[Array[Boom]] )

object Main {

  def main(args: Array[String]): Unit = {

    val dj = DottyJack()


    val inst = Array(15L)
    println(inst.getClass.getName)

    // val clazz = Class.forName("co.blocke.dottyjack.Foom")
    // val const = clazz.getConstructors
    // println("Constructors: "+const.toList)

    // val arg = Array(Array(Boom(1),Boom(2)))
    // println(">>> "+arg.getClass)

    // val z = Class.forName("[Lco.blocke.dottyjack.Boom;")
    // println("Z: "+z)

    // // val target = clazz.getConstructor(arg.getClass)
    // // println(target)

    // // println(target.newInstance(arg))
    // println("+++ "+Class.forName("scala.Array").getCanonicalName)

    //----

    // val c = Reflector.reflectOn[Foom].asInstanceOf[ScalaClassInfo]
    // println("Fields: "+c.fields.map(_.asInstanceOf[ScalaFieldInfo].constructorClass))
  }

}
