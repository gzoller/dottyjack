package co.blocke.dottyjack
package typeadapter
package classes

import model._
import co.blocke.dotty_reflection.TypeMemberInfo
import co.blocke.dotty_reflection.info._
import co.blocke.dotty_reflection._
import scala.collection.mutable


case class NonCaseClassTypeAdapter[T](
    info:                 RType,
    fieldMembersByName:   Map[String, ClassFieldMember[_,_]],
    argsTemplate:         Array[Object],
    fieldBitsTemplate:    mutable.BitSet,
    typeMembersByName:    Map[String, TypeMemberInfo],
    orderedFieldNames:    List[String],
    nonConstructorFields: List[ClassFieldMember[_,_]]
    // dbCollectionName:   Option[String]
)(implicit taCache: TypeAdapterCache) extends ScalaClassTypeAdapter[T]:

  private val classInfo = info.asInstanceOf[ScalaClassInfo]

  inline def fieldName(f: FieldInfo): String = f.annotations.get(CHANGE_ANNO).map(_("name")).getOrElse(f.name)

  def _read_createInstance(args: List[Object], captured: java.util.HashMap[String, String]): T = 
    // Build base object
    val asBuilt = 
      val const = classInfo.infoClass.getConstructors.head
      const.newInstance(args.take(classInfo.fields.size):_*).asInstanceOf[T]
    // Now call all the non-constructor setters
    nonConstructorFields.collect{ 
      // make sure f is known--in one special case it will not be: "captured" field for SJCapture should be ignored
      case f if fieldMembersByName.contains( fieldName(f.info) ) =>
        val setter = classInfo.infoClass.getMethod(f.info.name+"_$eq", fieldMembersByName(fieldName(f.info)).valueTypeAdapter.info.infoClass )
        args(f.info.index) match {
          case m: java.lang.reflect.Method => setter.invoke(asBuilt, m.invoke(asBuilt))
          case thing => setter.invoke(asBuilt, thing)
        }        
    }
    if isSJCapture
      asBuilt.asInstanceOf[SJCapture].captured = captured
    asBuilt

  def _read_updateFieldMembers( fmbn: Map[String, ClassFieldMember[_,_]]): ScalaClassTypeAdapter[T] = 
    this.copy(fieldMembersByName = fmbn)
