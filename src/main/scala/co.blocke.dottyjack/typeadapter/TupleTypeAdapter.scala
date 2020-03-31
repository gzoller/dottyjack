package co.blocke.dottyjack
package typeadapter

import model._
import co.blocke.dotty_reflection.infos._
import java.lang.reflect.Method
import scala.collection.mutable
import scala.util.matching.Regex

object TupleTypeAdapterFactory extends TypeAdapterFactory {

  private val tupleFullName: Regex = """scala.Tuple(\d+)""".r

  def matches(concrete: ConcreteType): Boolean = 
    concrete match {
      case ti: TupleInfo => true
      case _ => false
    }


  def makeTypeAdapter(concrete: ConcreteType)(implicit taCache: TypeAdapterCache): TypeAdapter[_] =
    val ti = concrete.asInstanceOf[TupleInfo]
    val fields = ti.tupleTypes.zipWithIndex.map{ (f,idx) => f match {
      case c: ConcreteType => 
        val javaClassField = ti.infoClass.getDeclaredField(s"_${idx+1}")
        javaClassField.setAccessible(true)
        TupleField(idx+1, javaClassField, taCache.typeAdapter(c))
      case f => throw new ScalaJackError(s"Unexpected non-Concrete tuple type ${f.getClass.getName}")
      }}
    TupleTypeAdapter(concrete, fields, ti.infoClass.getConstructors.head)

    /*
  override def typeAdapterOf[T](
      classSymbol: ClassSymbol,
      next:        TypeAdapterFactory
  )(implicit taCache: TypeAdapterCache, tt: TypeTag[T]): TypeAdapter[T] =
    classSymbol.fullName match {
      case tupleFullName(numberOfFieldsAsString) =>
        val numberOfFields = numberOfFieldsAsString.toInt
        val fieldTypes = tt.tpe.dealias.typeArgs

        val fields = for (i <- 0 until numberOfFields) yield {
          val fieldType = fieldTypes(i)
          val fieldTypeAdapter = taCache.typeAdapter(fieldType) match {
            case opt: OptionTypeAdapter[_] => opt.convertNullToNone()
            case ta                        => ta
          }

          val valueAccessorMethodSymbol =
            tt.tpe.member(TermName(s"_${i + 1}")).asMethod
          val valueAccessorMethod =
            Reflection.methodToJava(valueAccessorMethodSymbol)
          TupleField(
            i,
            fieldTypeAdapter,
            valueAccessorMethodSymbol,
            valueAccessorMethod
          )
        }

        val classMirror = currentMirror.reflectClass(classSymbol)
        val constructorMirror = classMirror.reflectConstructor(
          classSymbol.primaryConstructor.asMethod
        )

        TupleTypeAdapter(fields.toList, constructorMirror)
          .asInstanceOf[TypeAdapter[T]]

      case _ =>
        next.typeAdapterOf[T]
    }
    */

}

case class TupleTypeAdapter[T](
  info:        ConcreteType,
  fields:      List[TupleField[_]],
  constructor: java.lang.reflect.Constructor[T]
  ) extends TypeAdapter[T] with Collectionish {

  def read(parser: Parser): T =
    if (parser.peekForNull) then
      null.asInstanceOf[T]
    else
      constructor.newInstance(parser.expectTuple(fields): _*).asInstanceOf[T]

  // Create functions that know how to self-write each field.  The actual writing of each element
  // is done in TupleField where the specific field type F is known.
  def write[WIRE](
      t:      T,
      writer: Writer[WIRE],
      out:    mutable.Builder[WIRE, WIRE]): Unit =
    if (t == null)
      writer.writeNull(out)
    else
      writer.writeTuple(t, fields, out)
}
