package co.blocke.dottyjack
package model

import co.blocke.scala_reflection._

trait TypeAdapterFactory:
  def matches(concrete: RType): Boolean
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[_]
