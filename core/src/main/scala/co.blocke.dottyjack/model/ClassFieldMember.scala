package co.blocke.dottyjack
package model

import co.blocke.dotty_reflection.info._


case class ClassFieldMember[T](
  info:                               FieldInfo,
  // index:                              Int,
  // name:                               MemberName,
  // valueType:                          Type,
  valueTypeAdapter:                   TypeAdapter[T],
  // declaredValueType:                  Type,
  // valueAccessorMethod:                Method,
  // derivedValueClassConstructorMirror: Option[MethodMirror],
  // defaultValueMethod:                 Option[Method], // <-- Need a Java Method here to work with Java classes too!
  outerClass:                         java.lang.Class[_],  // class that "owns" this field
  dbKeyIndex:                         Option[Int],
  fieldMapName:                       Option[String]
  // ownerType:                          Type,
  // These 3 are only for Plain Classes -- unused for Case Classes
  // valueSetterMethodSymbol: Option[MethodSymbol], // for Scala non-constructor setters
  // valueSetterMethod:       Option[Method], // for Java beans setters
  // hasOptionalAnnotation:   Boolean              = false
)
