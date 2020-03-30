package co.blocke.dottyjack
package model

import co.blocke.dotty_reflection.infos._

trait TypeAdapterFactory:
  def matches(concrete: ConcreteType): Boolean
  def makeTypeAdapter(concrete: ConcreteType)(implicit taCache: TypeAdapterCache): TypeAdapter[_]
