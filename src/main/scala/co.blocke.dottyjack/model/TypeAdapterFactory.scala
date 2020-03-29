package co.blocke.dottyjack
package model

import co.blocke.dotty_reflection._

trait TypeAdapterFactory:
  def matches(tpe: TypeStructure): Boolean
  def makeTypeAdapter(tpe: TypeStructure): TypeAdapter[_]
