package com.github.pjfanning.jackson.reflection

import co.blocke.scala_reflection.RType
import co.blocke.scala_reflection.info.SealedTraitInfo
import com.fasterxml.jackson.databind.introspect.{Annotated, JacksonAnnotationIntrospector}
import com.fasterxml.jackson.databind.jsontype.NamedType

import scala.jdk.CollectionConverters.*

class ScalaReflectionAnnotationIntrospector extends JacksonAnnotationIntrospector {

  override def findSubtypes(a: Annotated): java.util.List[NamedType] = {
    val rtype = RType.of(a.getRawType)
    rtype match {
      case traitInfo: SealedTraitInfo =>
        traitInfo.children
          .map(ct => new NamedType(ct.infoClass))
          .toSeq.asJava
      case _ => None.orNull
    }
  }
}
