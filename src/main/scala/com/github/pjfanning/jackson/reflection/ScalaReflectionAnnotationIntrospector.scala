package com.github.pjfanning.jackson.reflection

import co.blocke.scala_reflection.RType
import co.blocke.scala_reflection.info.{ObjectInfo, ScalaClassInfo, SealedTraitInfo}
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.introspect.{Annotated, JacksonAnnotationIntrospector}
import com.fasterxml.jackson.databind.jsontype.NamedType

import scala.jdk.CollectionConverters.*

class ScalaReflectionAnnotationIntrospector extends JacksonAnnotationIntrospector {

  override def version(): Version = JacksonModule.version

  override def findSubtypes(a: Annotated): java.util.List[NamedType] = {
    val rtype = RType.of(a.getRawType)
    rtype match {
      case traitInfo: SealedTraitInfo =>
        traitInfo.children
          .map(ct => new NamedType(getClass(ct)))
          .toSeq.asJava
      case classInfo: ScalaClassInfo =>
        classInfo.children
          .map(ct => new NamedType(getClass(ct)))
          .toSeq.asJava
      case _ => None.orNull
    }
  }

  private def getClass(rtype: RType): Class[_] = rtype match {
    case objectInfo: ObjectInfo => getCompanionObjectClass(objectInfo.infoClass)
    case rt => rt.infoClass
  }

  private def getCompanionObjectClass(cls: Class[_]): Class[_] = {
    val cn = cls.getName
    if (cn.endsWith("$")) {
      cls
    } else {
      Class.forName(cn + '$', true, Thread.currentThread().getContextClassLoader)
    }
  }
}
