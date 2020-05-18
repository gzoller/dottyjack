package co.blocke.dottyjack
package model

import co.blocke.dotty_reflection.info._
import typeadapter.OptionTypeAdapter

case class ClassFieldMember[OWNER,T](
  info:                               FieldInfo,
  valueTypeAdapter:                   TypeAdapter[T],
  outerClass:                         java.lang.Class[OWNER],  // class that "owns" this field
  dbKeyIndex:                         Option[Int],
  fieldMapName:                       Option[String]
):
  def name: String = fieldMapName.getOrElse(info.name)
  lazy val isOptional: Boolean = valueTypeAdapter.isInstanceOf[OptionTypeAdapter[_]] // || hasOptionalAnnotation