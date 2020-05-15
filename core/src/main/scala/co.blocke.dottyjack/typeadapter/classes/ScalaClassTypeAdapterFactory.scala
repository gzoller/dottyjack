package co.blocke.dottyjack
package typeadapter
package classes

import co.blocke.dotty_reflection.info._
import co.blocke.dotty_reflection._
import model._
import scala.collection.mutable
import java.lang.reflect.Constructor

object ScalaClassTypeAdapterFactory extends TypeAdapterFactory:
  
  def matches(concrete: RType): Boolean = concrete match {
    case c: ScalaCaseClassInfo if !c.isValueClass => true
    case c: ScalaClassInfo => true
    case _ => false
  }

  final val CLASSCLASS = Class.forName("java.lang.Class")

  inline def bakeFieldMembersByName( 
      fields: List[FieldInfo], 
      constructor: Constructor[_], 
      infoClass: Class[_] )(implicit taCache: TypeAdapterCache
    ): (Map[String,ClassFieldMember[_,_]], mutable.BitSet, Array[Object], List[String])  =

    // Filter out any ignored fields and re-index them all
    val fieldsWeCareAbout = fields.filterNot(_.annotations.contains(IGNORE)).zipWithIndex.map{ (f,idx) => f.reIndex(idx) }
    
    val bits = mutable.BitSet()
    val args = new Array[Object](fieldsWeCareAbout.size)

    val fieldsByName = fieldsWeCareAbout.map { f =>
      val fieldTypeAdapter = f.fieldType match {
        case _: TypeSymbolInfo => taCache.typeAdapterOf(PrimitiveType.Scala_Any) // Any unresolved type symbols must be considered Any
        case t =>
          taCache.typeAdapterOf(t) match {
            // In certain situations, value classes need to be unwrapped, i.e. use the type adapter of their member.
            case vta: ValueClassTypeAdapter[_,_] if f.index < constructor.getParameterTypes().size =>   // value class in constructor
              val constructorParamClass = constructor.getParameterTypes()(f.index).getClass
              if constructorParamClass == vta.info.infoClass || constructorParamClass == CLASSCLASS then
                vta
              else
                vta.elementTypeAdapter
            case vta: ValueClassTypeAdapter[_,_] =>   // value class as body member
              val returnTypeClass = infoClass.getMethod(f.name).getReturnType
              if returnTypeClass == vta.info.infoClass || returnTypeClass == CLASSCLASS then
                vta
              else
                vta.elementTypeAdapter
            case other => other
          }
      }

      // See if there's a default value set and blip bits/args accordingly to "pre-set" these values
      if f.defaultValueAccessor.isDefined then
        args(f.index) = f.defaultValueAccessor.get()
      else if fieldTypeAdapter.defaultValue.isDefined then
        args(f.index) = fieldTypeAdapter.defaultValue.get.asInstanceOf[Object]
      else
        bits += f.index 

      val fieldMapName = f.annotations.get(CHANGE_ANNO).map(_("name"))
      fieldMapName.getOrElse(f.name) -> ClassFieldMember(
        f,
        fieldTypeAdapter,
        infoClass,
        None,  // TODO
        fieldMapName
      )
    }.toMap
    (fieldsByName, bits, args, fieldsWeCareAbout.map( f => f.annotations.get(CHANGE_ANNO).map(_("name")).getOrElse(f.name) ))
  
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[_] =
    concrete match {
      case classInfo: ScalaCaseClassInfo =>
        val (fieldMembersByName, bits, args, orderedFieldNames) = bakeFieldMembersByName(classInfo.fields, classInfo.constructor, classInfo.infoClass)
        CaseClassTypeAdapter(
          concrete,
          fieldMembersByName,
          args,
          bits,
          classInfo.typeMembers.map( tmem => (tmem.name, tmem.asInstanceOf[TypeMemberInfo]) ).toMap,
          orderedFieldNames
        )

      case classInfo: ScalaClassInfo =>
        val allFields = {
          if classInfo.hasMixin(SJ_CAPTURE) then
            (classInfo.fields ++ (classInfo.nonConstructorFields.filterNot(_.name == "captured")))
          else
            classInfo.fields ++ classInfo.nonConstructorFields
        }
        val (fieldMembersByName, bits, args, orderedFieldNames) = bakeFieldMembersByName(allFields, classInfo.constructor, classInfo.infoClass)
        val paramSize = classInfo.constructor.getParameterTypes().size
        NonCaseClassTypeAdapter(
          concrete,
          fieldMembersByName,
          args,
          bits,
          classInfo.typeMembers.map( tmem => (tmem.name, tmem.asInstanceOf[TypeMemberInfo]) ).toMap,
          orderedFieldNames,
          fieldMembersByName.values.filter(_.info.index >= paramSize).toList
        )
    }
