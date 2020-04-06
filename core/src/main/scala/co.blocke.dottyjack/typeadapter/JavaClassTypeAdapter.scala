package co.blocke.dottyjack
package typeadapter

import model._

import scala.collection.mutable
import co.blocke.dotty_reflection._
import co.blocke.dotty_reflection.infos._

object JavaClassTypeAdapterFactory extends TypeAdapterFactory:
  def matches(concrete: ConcreteType): Boolean = 
    concrete match {
      case _: JavaClassInfo => true
      case _ => false
    }
  def makeTypeAdapter(concrete: ConcreteType)(implicit taCache: TypeAdapterCache): TypeAdapter[_] =
    val classInfo = concrete.asInstanceOf[ClassInfo]
    val bits = mutable.BitSet()
    val args = new Array[Object](classInfo.fields.size)

    classInfo.fields.map( f => bits += f.index )
    val fieldMembersByName = 
      concrete.asInstanceOf[JavaClassInfo].fields.map{ f => 
        val fieldMember: ClassFieldMember[_] = f.fieldType match {
          case c: ConcreteType =>
            ClassFieldMember(
              f,
              taCache.typeAdapterOf(c),
              None,  // TODO
              None,  // TODO
              None   // TODO
            )
          case c => throw new ScalaJackError(s"Concrete type expected for class ${concrete.name} field ${f.name}.  ${c.getClass.getName} was found.")
        }
        f.name -> fieldMember}.toMap
    JavaClassTypeAdapter(concrete, args, bits, fieldMembersByName)


case class JavaClassTypeAdapter[J](
    info:             ConcreteType,
    argsTemplate:       Array[Object],
    fieldBitsTemplate:  mutable.BitSet,
    fieldMembersByName: Map[String, ClassFieldMember[_]]
  )(implicit taCache: TypeAdapterCache) extends ClassTypeAdapterBase[J]:

  val javaClassInfo = info.asInstanceOf[JavaClassInfo]
  private val orderedFieldNames = javaClassInfo.fields.map(_.name)
  val isSJCapture = javaClassInfo.hasMixin("co.blocke.dottyjack.SJCapture")

  def read(parser: Parser): J =
    if (parser.peekForNull) then
      null.asInstanceOf[J]
    else 
      val (foundBits, args, captured) = parser.expectObject(
        this,
        taCache.jackFlavor.defaultHint
      )
      if (foundBits.isEmpty) then
        val asBuilt = javaClassInfo.constructWith[J](args)
        if isSJCapture
          asBuilt.asInstanceOf[SJCapture].captured = captured
        asBuilt
      else
        parser.backspace()
        throw new ScalaJackError(
          parser.showError(
            s"Class ${info.name} missing required fields: " + foundBits
              .map(b => orderedFieldNames(b))
              .mkString(", ")
          )
        )
        
  def write[WIRE](
      t:      J,
      writer: Writer[WIRE],
      out:    mutable.Builder[WIRE, WIRE]): Unit =
    val extras = scala.collection.mutable.ListBuffer.empty[(String, ExtraFieldValue[_])]
    writer.writeObject(
      t,
      orderedFieldNames,
      fieldMembersByName,
      out,
      extras.toList)