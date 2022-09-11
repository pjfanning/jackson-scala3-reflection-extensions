package com.github.pjfanning.jackson.reflection

import com.fasterxml.jackson.core.Version

trait ScalaReflectAnnotationIntrospectorModule extends JacksonModule {
  this += { _.appendAnnotationIntrospector(new ScalaReflectionAnnotationIntrospector) }
}

object ScalaReflectAnnotationIntrospectorModule extends ScalaReflectAnnotationIntrospectorModule
