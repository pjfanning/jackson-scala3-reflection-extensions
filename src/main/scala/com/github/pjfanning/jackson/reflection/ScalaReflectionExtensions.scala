package com.github.pjfanning.jackson.reflection

import co.blocke.scala_reflection.RType
import co.blocke.scala_reflection.impl.CollectionRType
import co.blocke.scala_reflection.info.{ClassInfo, MapLikeInfo, ScalaOptionInfo}
import com.fasterxml.jackson.core.{JsonParser, TreeNode}
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.{JavaType, MappingIterator, ObjectMapper, ObjectReader, ObjectWriter}
import com.fasterxml.jackson.module.scala.JavaTypeable
import com.fasterxml.jackson.module.scala.introspect.ScalaAnnotationIntrospectorModule
import com.fasterxml.jackson.databind.`type`.{ArrayType, CollectionLikeType, ReferenceType}

import java.io.{File, InputStream, Reader}
import java.net.URL
import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scala.reflect.ClassTag

object ScalaReflectionExtensions {
  def ::(o: JsonMapper): JsonMapper with ScalaReflectionExtensions = new JsonMapperMixin(o)
  def ::(o: ObjectMapper): ObjectMapper with ScalaReflectionExtensions = new ObjectMapperMixin(o)

  final class JsonMapperMixin private[ScalaReflectionExtensions](mapper: JsonMapper)
    extends JsonMapper(mapper) with ScalaReflectionExtensions

  final class ObjectMapperMixin private[ScalaReflectionExtensions](mapper: ObjectMapper)
    extends ObjectMapper(mapper) with ScalaReflectionExtensions

  def registerInnerTypes(rtype: RType): Unit = rtype match {
    case classInfo: ClassInfo => registerInnerTypes(classInfo)
    case _ =>
  }

  def registerInnerTypes(classInfo: ClassInfo): Unit = registerInnerTypes(classInfo, Set.empty)

  private def registerInnerTypes(classInfo: ClassInfo, registered: Set[Class[_]]): Unit = {
    if (!registered.contains(classInfo.infoClass)) {
      val updatedRegistered = registered + classInfo.infoClass
      classInfo.fields.foreach { fieldInfo =>
        fieldInfo.fieldType match {
          case optionInfo: ScalaOptionInfo =>
            registerReferencedValueType(classInfo.infoClass, fieldInfo.name,
              getInnerType(optionInfo.optionParamType), updatedRegistered)
          case mapInfo: MapLikeInfo =>
            registerReferencedValueType(classInfo.infoClass, fieldInfo.name,
              getInnerType(mapInfo.elementType2), updatedRegistered)
          case seqInfo: CollectionRType =>
            registerReferencedValueType(classInfo.infoClass, fieldInfo.name,
              getInnerType(seqInfo.elementType), updatedRegistered)
          case fclz: ClassInfo => registerInnerTypes(fclz, updatedRegistered)
          case _ =>
        }
      }
    }
  }

  @tailrec
  private def getInnerType(rtype: RType): RType = rtype match {
    case optionInfo: ScalaOptionInfo => getInnerType(optionInfo.optionParamType)
    case mapInfo: MapLikeInfo => getInnerType(mapInfo.elementType2)
    case seqInfo: CollectionRType => getInnerType(seqInfo.elementType)
    case _ => rtype
  }

  private def registerReferencedValueType(clazz: Class[_], fieldName: String, referencedType: RType,
                                          registeredClasses: Set[Class[_]]): Unit = {
    val referenceTypeClass = referencedType.infoClass
    if (referenceTypeClass.isPrimitive) {
      ScalaAnnotationIntrospectorModule.registerReferencedValueType(clazz, fieldName, referenceTypeClass)
    } else {
      referencedType match {
        case classInfo: ClassInfo => registerInnerTypes(classInfo, registeredClasses)
        case _ =>
      }
    }
  }
}

trait ScalaReflectionExtensions {
  self: ObjectMapper =>

  private val registeredClasses =
    java.util.Collections.newSetFromMap(
      new java.util.concurrent.ConcurrentHashMap[Class[_], java.lang.Boolean]).asScala

  /**
   * Method to deserialize JSON content into a Java type, reference
   * to which is passed as argument. Type is passed using so-called
   * "super type token"
   * and specifically needs to be used if the root type is a
   * parameterized (generic) container type.
   */
  def readValue[T: JavaTypeable](jp: JsonParser): T = {
    readValue(jp, constructType[T])
  }


  /**
   * Method for reading sequence of Objects from parser stream.
   * Sequence can be either root-level "unwrapped" sequence (without surrounding
   * JSON array), or a sequence contained in a JSON Array.
   * In either case [[com.fasterxml.jackson.core.JsonParser]] must point to the first token of
   * the first element, OR not point to any token (in which case it is advanced
   * to the next token). This means, specifically, that for wrapped sequences,
   * parser MUST NOT point to the surrounding <code>START_ARRAY</code> but rather
   * to the token following it.
   * <p>
   * Note that [[com.fasterxml.jackson.databind.ObjectReader]] has more complete set of variants.
   */
  def readValues[T: JavaTypeable](jp: JsonParser): MappingIterator[T] = {
    readValues(jp, constructType[T])
  }

  def treeToValue[T: JavaTypeable](n: TreeNode): T = {
    treeToValue(n, constructType[T])
  }

  def readValue[T: JavaTypeable](src: File): T = {
    readValue(src, constructType[T])
  }

  def readValue[T: JavaTypeable](src: URL): T = {
    readValue(src, constructType[T])
  }

  def readValue[T: JavaTypeable](content: String): T = {
    readValue(content, constructType[T])
  }

  def readValue[T: JavaTypeable](src: Reader): T = {
    readValue(src, constructType[T])
  }

  def readValue[T: JavaTypeable](src: InputStream): T = {
    readValue(src, constructType[T])
  }

  def readValue[T: JavaTypeable](src: Array[Byte]): T = {
    readValue(src, constructType[T])
  }

  def readValue[T: JavaTypeable](src: Array[Byte], offset: Int, len: Int): T = {
    readValue(src, offset, len, constructType[T])
  }

  def readValue[T: JavaTypeable](src: File, rType: RType): T = {
    readValue(src, constructType[T](rType))
  }

  def readValue[T: JavaTypeable](src: URL, rType: RType): T = {
    readValue(src, constructType[T](rType))
  }

  def readValue[T: JavaTypeable](content: String, rType: RType): T = {
    readValue(content, constructType[T](rType))
  }

  def readValue[T: JavaTypeable](src: Reader, rType: RType): T = {
    readValue(src, constructType[T](rType))
  }

  def readValue[T: JavaTypeable](src: InputStream, rType: RType): T = {
    readValue(src, constructType[T](rType))
  }

  def readValue[T: JavaTypeable](src: Array[Byte], rType: RType): T = {
    readValue(src, constructType[T](rType))
  }

  def readValue[T: JavaTypeable](src: Array[Byte], offset: Int, len: Int, rType: RType): T = {
    readValue(src, offset, len, constructType[T](rType))
  }

  def updateValue[T: JavaTypeable](valueToUpdate: T, src: File): T = {
    objectReaderFor(valueToUpdate).readValue(src)
  }

  def updateValue[T: JavaTypeable](valueToUpdate: T, src: URL): T = {
    objectReaderFor(valueToUpdate).readValue(src)
  }

  def updateValue[T: JavaTypeable](valueToUpdate: T, content: String): T = {
    objectReaderFor(valueToUpdate).readValue(content)
  }

  def updateValue[T: JavaTypeable](valueToUpdate: T, src: Reader): T = {
    objectReaderFor(valueToUpdate).readValue(src)
  }

  def updateValue[T: JavaTypeable](valueToUpdate: T, src: InputStream): T = {
    objectReaderFor(valueToUpdate).readValue(src)
  }

  def updateValue[T: JavaTypeable](valueToUpdate: T, src: Array[Byte]): T = {
    objectReaderFor(valueToUpdate).readValue(src)
  }

  def updateValue[T: JavaTypeable](valueToUpdate: T, src: Array[Byte], offset: Int, len: Int): T = {
    objectReaderFor(valueToUpdate).readValue(src, offset, len)
  }

  private def objectReaderFor[T: JavaTypeable](valueToUpdate: T): ObjectReader = {
    readerForUpdating(valueToUpdate).forType(constructType[T])
  }

  /*
   **********************************************************
   * Extended Public API: constructing ObjectWriters
   * for more advanced configuration
   **********************************************************
   */

  /**
   * Factory method for constructing [[com.fasterxml.jackson.databind.ObjectWriter]] that will
   * serialize objects using specified JSON View (filter).
   */
  def writerWithView[T: ClassTag]: ObjectWriter = {
    writerWithView(classFor[T])
  }

  /**
   * Factory method for constructing {@link com.fasterxml.jackson.databind.ObjectWriter} that will
   * serialize objects using specified root type, instead of actual
   * runtime type of value. Type must be a super-type of runtime type.
   * <p>
   * Main reason for using this method is performance, as writer is able
   * to pre-fetch serializer to use before write, and if writer is used
   * more than once this avoids addition per-value serializer lookups.
   */
  def writerFor[T: JavaTypeable]: ObjectWriter = {
    writerFor(constructType[T])
  }

  /*
   **********************************************************
   * Extended Public API: constructing ObjectReaders
   * for more advanced configuration
   **********************************************************
   */

  /**
   * Factory method for constructing [[com.fasterxml.jackson.databind.ObjectReader]] that will
   * read or update instances of specified type
   */
  def readerFor[T: JavaTypeable]: ObjectReader = {
    readerFor(constructType[T])
  }

  /**
   * Factory method for constructing [[com.fasterxml.jackson.databind.ObjectReader]] that will
   * deserialize objects using specified JSON View (filter).
   */
  def readerWithView[T: ClassTag]: ObjectReader = {
    readerWithView(classFor[T])
  }

  /*
   **********************************************************
   * Extended Public API: convenience type conversion
   **********************************************************
   */

  /**
   * Convenience method for doing two-step conversion from given value, into
   * instance of given value type. This is functionality equivalent to first
   * serializing given value into JSON, then binding JSON data into value
   * of given type, but may be executed without fully serializing into
   * JSON. Same converters (serializers, deserializers) will be used as for
   * data binding, meaning same object mapper configuration works.
   *
   * @throws IllegalArgumentException If conversion fails due to incompatible type;
   *                                  if so, root cause will contain underlying checked exception data binding
   *                                  functionality threw
   */
  def convertValue[T: JavaTypeable](fromValue: Any): T = {
    convertValue(fromValue, constructType[T])
  }

  def constructType[T: JavaTypeable]: JavaType = {
    val javaType = implicitly[JavaTypeable[T]].asJavaType(getTypeFactory)
    javaType match {
      case rt: ReferenceType =>
      case at: ArrayType =>
      case ct: CollectionLikeType =>
      case _ => {
        val clazz = javaType.getRawClass
        if (!registeredClasses.contains(clazz)) {
          RType.of(clazz) match {
            case classInfo: ClassInfo => ScalaReflectionExtensions.registerInnerTypes(classInfo)
            case _ =>
          }
        }
      }
    }
    javaType
  }

  private def constructType[T: JavaTypeable](rType: RType): JavaType = {
    val javaType = implicitly[JavaTypeable[T]].asJavaType(getTypeFactory)
    rType match {
      case classInfo: ClassInfo => {
        val clazz = javaType.getRawClass
        if (!registeredClasses.contains(clazz)) {
          ScalaReflectionExtensions.registerInnerTypes(classInfo)
        }
      }
    }
    javaType
  }

  private def classFor[T: ClassTag]: Class[T] = {
    implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]]
  }
}
