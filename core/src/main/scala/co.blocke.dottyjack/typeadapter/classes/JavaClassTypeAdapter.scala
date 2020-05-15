package co.blocke.dottyjack
package typeadapter
package classes

import model._

import scala.collection.mutable
import co.blocke.dotty_reflection._
import co.blocke.dotty_reflection.info._
import scala.util.Try

object JavaClassTypeAdapterFactory extends TypeAdapterFactory:
  def matches(concrete: RType): Boolean = 
    concrete match {
      case _: JavaClassInfo => true
      case _ => false
    }
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[_] =
    val classInfo = concrete.asInstanceOf[ClassInfo]

    // Filter out any ignored fields and re-index them all
    val fieldsWeCareAbout = classInfo.fields.filterNot(_.annotations.contains(IGNORE)).zipWithIndex.map{ (f,idx) => f.reIndex(idx) }

    val bits = mutable.BitSet()
    val args = new Array[Object](fieldsWeCareAbout.size)

    Try(classInfo.asInstanceOf[JavaClassInfo].infoClass.getConstructor()).toOption.orElse(
      throw new ScalaJackError("ScalaJack does not support Java classes with a non-empty constructor.")
    )

    val fieldMembersByName = 
      fieldsWeCareAbout.map { f =>
        f.fieldType match {
          case c: TypeSymbolInfo => throw new ScalaJackError(s"Concrete type expected for class ${concrete.name} field ${f.name}.  ${c.getClass.getName} was found.")
          case c =>
            bits += f.index
            val fieldMapName = f.annotations.get(CHANGE_ANNO).map(_("name"))             
            fieldMapName.getOrElse(f.name) -> ClassFieldMember(
              f,
              taCache.typeAdapterOf(c),
              classInfo.infoClass,  // TODO
              None,  // TODO
              fieldMapName
            )
        }
      }.toMap

    JavaClassTypeAdapter(concrete, args, bits, fieldMembersByName, fieldsWeCareAbout.map( f => f.annotations.get(CHANGE_ANNO).map(_("name")).getOrElse(f.name)))


case class JavaClassTypeAdapter[J](
    info:               RType,
    argsTemplate:       Array[Object],
    fieldBitsTemplate:  mutable.BitSet,
    fieldMembersByName: Map[String, ClassFieldMember[_,_]],
    orderedFieldNames:  List[String]
  )(implicit taCache: TypeAdapterCache) extends ClassTypeAdapterBase[J]:

  val javaClassInfo = info.asInstanceOf[JavaClassInfo]
  // private val orderedFieldNames = javaClassInfo.fields.map(_.name)
  val isSJCapture = javaClassInfo.hasMixin(SJ_CAPTURE)

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