package co.blocke.dottyjack
package model

import co.blocke.dotty_reflection.infos._
import scala.collection.mutable

/** This helps fix the concurrent/recursion error on maps.  This lets the TypeAdapter resolve later (i.e. Lazy)
 */
case class LazyTypeAdapter[T](taCache: TypeAdapterCache, info: ConcreteType)
  extends TypeAdapter[T] {

  var resolvedTypeAdapter: TypeAdapter[T] = _

  override def resolved: TypeAdapter[T] = {
    var typeAdapter = resolvedTypeAdapter

    // $COVERAGE-OFF$Can't really test as this is triggered by race condition, if it can happen at all.
    if (typeAdapter == null) {
      typeAdapter =
        taCache.typeAdapter(info).resolved.asInstanceOf[TypeAdapter[T]]
      if (typeAdapter.isInstanceOf[LazyTypeAdapter[_]]) {
        throw new IllegalStateException(
          s"Type adapter for ${info.name} is still being built"
        )
      }
      resolvedTypeAdapter = typeAdapter
    }
    // $COVERAGE-ON$

    typeAdapter
  }

  def read(parser: Parser): T = resolved.read(parser)
  def write[WIRE](
      t:      T,
      writer: Writer[WIRE],
      out:    mutable.Builder[WIRE, WIRE]): Unit =
    resolved.write(t, writer, out)
}
