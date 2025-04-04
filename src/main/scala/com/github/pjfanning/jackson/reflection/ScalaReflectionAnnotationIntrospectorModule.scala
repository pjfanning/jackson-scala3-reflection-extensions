package com.github.pjfanning.jackson.reflection

trait ScalaReflectAnnotationIntrospectorModule extends JacksonModule {
  this += { _.appendAnnotationIntrospector(new ScalaReflectionAnnotationIntrospector) }
}

object ScalaReflectAnnotationIntrospectorModule extends ScalaReflectAnnotationIntrospectorModule
