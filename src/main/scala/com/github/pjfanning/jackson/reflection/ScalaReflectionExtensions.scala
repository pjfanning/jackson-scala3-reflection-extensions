package com.github.pjfanning.jackson.reflection

import co.blocke.scala_reflection.RType
import co.blocke.scala_reflection.info.{ClassInfo, ScalaOptionInfo, SeqLikeInfo}
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.{JavaType, ObjectMapper}
import com.fasterxml.jackson.module.scala.{ClassTagExtensions, JavaTypeable}
import com.fasterxml.jackson.module.scala.introspect.ScalaAnnotationIntrospector

object ScalaReflectionExtensions {
  def ::(o: JsonMapper) = new Mixin(o)
  final class Mixin private[ScalaReflectionExtensions](mapper: JsonMapper)
    extends JsonMapper(mapper.rebuild().build()) with ScalaReflectionExtensions
}

trait ScalaReflectionExtensions {
  self: ObjectMapper =>

  private def registeredClasses = scala.collection.mutable.HashSet[Class[_]]()

  def readValue[T: JavaTypeable](json: String): T = {
    readValue(json, constructType[T])
  }

  def readValue[T: JavaTypeable](jp: JsonParser): T = {
    readValue(jp, constructType[T])
  }

  private def constructType[T: JavaTypeable]: JavaType = {
    val javaType = implicitly[JavaTypeable[T]].asJavaType(getTypeFactory)
    val clazz = javaType.getRawClass
    if (!registeredClasses.contains(clazz)) {
      RType.of(clazz) match {
        case classInfo: ClassInfo => registerInnerTypes(classInfo)
        case _ =>
      }
    }
    javaType
  }

  private def registerInnerTypes(classInfo: ClassInfo): Unit = {
    if (!registeredClasses.contains(classInfo.infoClass)) {
      classInfo.fields.foreach { fieldInfo =>
        fieldInfo.fieldType match {
          case optionInfo: ScalaOptionInfo =>
            ScalaAnnotationIntrospector.registerReferencedValueType(classInfo.infoClass, fieldInfo.name, optionInfo.optionParamType.infoClass)
          case seqInfo: SeqLikeInfo =>
            ScalaAnnotationIntrospector.registerReferencedValueType(classInfo.infoClass, fieldInfo.name, seqInfo.elementType.infoClass)
          case _ =>
        }
        fieldInfo.fieldType match {
          case fclz: ClassInfo => registerInnerTypes(fclz)
          case _ =>
        }
      }
      registeredClasses.add(classInfo.infoClass)
    }
  }
}
