package co.blocke.dottyjack
package model

import co.blocke.dotty_reflection._

trait TypeAdapterFactory:
  def matches(concrete: Transporter.RType): Boolean
  def makeTypeAdapter(concrete: Transporter.RType)(implicit taCache: TypeAdapterCache): TypeAdapter[_]
