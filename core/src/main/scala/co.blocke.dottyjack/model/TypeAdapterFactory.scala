package co.blocke.dottyjack
package model

import co.blocke.dotty_reflection._

trait TypeAdapterFactory:
  def matches(concrete: RType): Boolean
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[_]
