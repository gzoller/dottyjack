package co.blocke.dottyjack
package model

// import util.BijectiveFunctionHelpers._

trait HintValueModifier extends HintBijective

/**
 * Do-nothing modifier (default if none specified
 */
object DefaultHintModifier extends HintValueModifier {
  def apply(rawHint: String): String = rawHint
  def unapply(hintFieldType: String): String = hintFieldType
}
// object DefaultHintModifier extends HintValueModifier {
//   def apply(rawHint: String): Class[_] = Class.forName(rawHint)
//   def unapply(hintFieldType: Class[_]): String = hintFieldType.getName
// }

/* TODO
/**
 * Convenience modifier that transforms a hint value string into a fully-qualified class name (and the reverse)
 * using passed-in transformation functions.  The appropriate Bijective is created under the covers.
 */
case class ClassNameHintModifier(
    hintToClassname: (String) => String,
    classNameToHint: (String) => String)
  extends HintValueModifier {
  def apply(rawHint: String): universe.Type =
    fullNameToType.apply(hintToClassname(rawHint)) // May explode
  def unapply(hintFieldType: Type): String =
    classNameToHint(fullNameToType.unapply(hintFieldType)) // May explode
}

/**
 * Convenience modifier that transforms a map of string hint values to their respective types.
 * Note there is a necessary assumption that the mapping is 1-to-1.  If not you'll need to create the
 * Bijective function yourself with whatever logic you need, and not use this class.
 */
case class StringMatchHintModifier(hintToType: Map[String, Type])
  extends HintValueModifier {
  val typeToHint: Map[universe.Type, String] = hintToType.map(_.swap)
  def apply(rawHint: String): universe.Type = hintToType(rawHint) // May explode
  def unapply(hintFieldType: Type): String =
    typeToHint(hintFieldType) // May explode
}
*/