package co.blocke.dottyjack
package typeadapter

import model._
import collection._

import co.blocke.dotty_reflection._
import co.blocke.dotty_reflection.infos._


object CollectionTypeAdapterFactory extends TypeAdapterFactory:
  def matches(concrete: ConcreteType): Boolean = 
    concrete match {
      case _: CollectionType => true
      case _ => false
    }
  def makeTypeAdapter(concrete: ConcreteType)(implicit taCache: TypeAdapterCache): TypeAdapter[_] =
    concrete match {
      case c: SeqLikeInfo => 
        val elementInfo = c.elementType.asInstanceOf[ConcreteType]
        val companionClass = Class.forName(c.infoClass.getName+"$")
        val companionInstance = companionClass.getField("MODULE$").get(companionClass)
        val builderMethod = companionClass.getMethod("newBuilder")
        SeqLikeTypeAdapter(concrete, elementInfo.isInstanceOf[OptionInfo], taCache.typeAdapterOf(elementInfo), companionInstance, builderMethod)
        
      case c: MapLikeInfo =>
        val companionClass = Class.forName(c.infoClass.getName+"$")
        val companionInstance = companionClass.getField("MODULE$").get(companionClass)
        val builderMethod = companionClass.getMethod("newBuilder")

        val jackFlavor = taCache.jackFlavor
        val keyTypeAdapter = taCache.typeAdapterOf(c.elementType1.asInstanceOf[ConcreteType])
        // Wrap Map keys in a StringWrapTypeAdapter?
        val finalKeyTypeAdapter =
          if (keyTypeAdapter.isInstanceOf[AnyTypeAdapter])
            jackFlavor.anyMapKeyTypeAdapter
          else if (!jackFlavor.stringifyMapKeys
            || keyTypeAdapter.isInstanceOf[Stringish]
            // TODO
            // || keyType <:< typeOf[Enumeration#Value] && !enumsAsInt
            // || keyTypeAdapter.isInstanceOf[ValueClassTypeAdapter[_, _]] 
            // && keyTypeAdapter.asInstanceOf[ValueClassTypeAdapter[_, _]].sourceTypeAdapter.isInstanceOf[Stringish]
            || (keyTypeAdapter.isInstanceOf[OptionTypeAdapter[_]] && keyTypeAdapter.asInstanceOf[OptionTypeAdapter[_]].valueIsStringish()))
            keyTypeAdapter
          else 
            jackFlavor.stringWrapTypeAdapterFactory(keyTypeAdapter)
        val valueTypeAdapter = taCache.typeAdapterOf(c.elementType2.asInstanceOf[ConcreteType])

        // Note: We include Any here because Any *could* be an Option, so we must include it as a possibility
        val keyIsOptionalOrAny =
          keyTypeAdapter.isInstanceOf[OptionTypeAdapter[_]] ||
            (keyTypeAdapter.isInstanceOf[StringWrapTypeAdapter[_]] && keyTypeAdapter
              .asInstanceOf[StringWrapTypeAdapter[_]]
              .wrappedTypeAdapter
              .isInstanceOf[OptionTypeAdapter[_]]) ||
              keyTypeAdapter == taCache.jackFlavor.anyMapKeyTypeAdapter

        val valueIsOptionalOrAny = valueTypeAdapter
          .isInstanceOf[OptionTypeAdapter[_]] ||
          valueTypeAdapter.isInstanceOf[AnyTypeAdapter]

        MapLikeTypeAdapter(
          concrete, 
          keyIsOptionalOrAny,
          valueIsOptionalOrAny,
          finalKeyTypeAdapter, 
          valueTypeAdapter,
          companionInstance, 
          builderMethod)

      // case c: JavaSetInfo =>
      // case c: JavaListInfo =>
      // case c: JavaQueueInfo =>
      // case c: JavaMapInfo =>
    }
