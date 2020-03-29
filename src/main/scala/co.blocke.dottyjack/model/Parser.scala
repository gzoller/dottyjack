package co.blocke.dottyjack
package model

// TODO
// import typeadapter.ClassTypeAdapterBase
import scala.collection.mutable

trait Parser {
  type WIRE

  // TODO
  //val jackFlavor: JackFlavor[WIRE] // This is needed and used by permissive type adapters

  def expectString(nullOK: Boolean = true): String
  def expectList[K, TO](
      KtypeAdapter: TypeAdapter[K],
      builder:      mutable.Builder[K, TO]): TO
  // TODO
  // def expectTuple(
  //     readFns: List[typeadapter.TupleTypeAdapterFactory.TupleField[_]]
  // ): List[Any]
  def expectMap[K, V, TO](
      keyTypeAdapter:   TypeAdapter[K],
      valueTypeAdapter: TypeAdapter[V],
      builder:          mutable.Builder[(K, V), TO]): TO
  // TODO
  // def expectObject(
  //     classBase: ClassTypeAdapterBase[_],
  //     hintLabel: String
  // ): (mutable.BitSet, Array[Any], java.util.HashMap[String, _])
  def expectBoolean(): Boolean
  def expectNumber(nullOK: Boolean = false): String
  def peekForNull: Boolean // peek-ahead to find null
  // TODO
  //def scanForHint(hint: String, converterFn: HintBijective): Type

  // For embedded type members.  Convert the type member into runtime "actual" type, e.g. T --> Foo
  // TODO
  // def resolveTypeMembers(
  //     typeMembersByName: Map[String, ClassHelper.TypeMember[_]],
  //     converterFn:       HintBijective
  // ): Map[Type, Type] // Returns Map[Type Signature Type (e.g. 'T'), Type]

  def showError(msg: String): String
  def backspace(): Unit
  def mark(): Int
  def revertToMark(mark: Int): Unit
  def nextIsString: Boolean
  def nextIsNumber: Boolean
  def nextIsObject: Boolean
  def nextIsArray: Boolean
  def nextIsBoolean: Boolean
  def subParser(input: WIRE): Parser
  def sourceAsString: String
}