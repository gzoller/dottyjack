package co.blocke.dottyjack
package typeadapter
package classes

import model._
import co.blocke.dotty_reflection.TypeMemberInfo
import co.blocke.dotty_reflection.info._
import co.blocke.dotty_reflection._
import scala.collection.mutable


case class NonCaseClassTypeAdapter[T](
    info:               RType,
    fieldMembersByName: Map[String, ClassFieldMember[_]],
    argsTemplate:       Array[Object],
    fieldBitsTemplate:  mutable.BitSet,
    typeMembersByName:  Map[String, TypeMemberInfo]
    // dbCollectionName:   Option[String]
)(implicit taCache: TypeAdapterCache) extends ScalaClassTypeAdapter[T]:

  private val classInfo = info.asInstanceOf[ScalaClassInfo]

  // TODO: Account for @Change annotation on field names
  override val orderedFieldNames = classInfo.fields.map(_.name) ::: classInfo.nonConstructorFields.map(_.name)

  def _read_createInstance(args: List[Object], captured: java.util.HashMap[String, _]): T = 
    // Build base object
    val asBuilt = 
      val const = classInfo.infoClass.getConstructors.head
      if (classInfo.typeMembers.nonEmpty) then
        // TODO: This does nothing!!!  Maybe a relic???
        val originalArgTypes = classInfo.fields.map(_.fieldType.infoClass)
        const.newInstance(args.take(classInfo.fields.size):_*).asInstanceOf[T]
      else
        const.newInstance(args.take(classInfo.fields.size):_*).asInstanceOf[T]
    // Now call all the non-constructor setters
    classInfo.nonConstructorFields.map{ f =>
        val setter = classInfo.infoClass.getMethod(f.name+"_$eq", f.fieldType.infoClass)
        args(f.index) match {
          case m: java.lang.reflect.Method => setter.invoke(asBuilt, m.invoke(asBuilt))
          case thing => setter.invoke(asBuilt, thing)
        }
        
      }
    if isSJCapture
      asBuilt.asInstanceOf[SJCapture].captured = captured
    asBuilt

  def _read_updateFieldMembers( fmbn: Map[String, ClassFieldMember[_]]): ScalaClassTypeAdapter[T] = 
    this.copy(fieldMembersByName = fmbn)

    /*

      ClassTypeAdapterBase {T}

        ScalaClassTypeAdapter {T}

          CaseClassTypeAdapter {C}
          NonCaseClassTypeAdapter {C}

        JavaClassTypeAdpater {C}

    */