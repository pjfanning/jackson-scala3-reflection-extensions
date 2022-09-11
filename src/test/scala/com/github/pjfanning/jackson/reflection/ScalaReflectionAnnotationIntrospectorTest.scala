package com.github.pjfanning.jackson.reflection

import com.fasterxml.jackson.databind.introspect.AnnotatedClassResolver
import com.fasterxml.jackson.databind.json.JsonMapper
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.jdk.CollectionConverters.*

class ScalaReflectionAnnotationIntrospectorTest extends AnyFlatSpec with Matchers {
  "ScalaReflectAnnotationIntrospector" should "find sub types for unannotated.Color" in {
    val introspector = new ScalaReflectionAnnotationIntrospector
    val mapper = JsonMapper.builder().build()
    val colorType = mapper.constructType(classOf[unannotated.Color])
    val annotatedColor = AnnotatedClassResolver.resolve(
      mapper.getDeserializationConfig, colorType, mapper.getDeserializationConfig)
    val subtypes = introspector.findSubtypes(annotatedColor).asScala.toSeq.map(_.getType)
    subtypes should have size 3
    subtypes(0) shouldEqual unannotated.Red.getClass
    subtypes(1) shouldEqual unannotated.Green.getClass
    subtypes(2) shouldEqual unannotated.Blue.getClass
  }

  it should "find sub types for annotated.Color" in {
    val introspector = new ScalaReflectionAnnotationIntrospector
    val mapper = JsonMapper.builder().build()
    val colorType = mapper.constructType(classOf[annotated.Color])
    val annotatedColor = AnnotatedClassResolver.resolve(
      mapper.getDeserializationConfig, colorType, mapper.getDeserializationConfig)
    val subtypes = introspector.findSubtypes(annotatedColor).asScala.toSeq.map(_.getType)
    subtypes should have size 3
    subtypes(0) shouldEqual annotated.Red.getClass
    subtypes(1) shouldEqual annotated.Green.getClass
    subtypes(2) shouldEqual annotated.Blue.getClass
  }

  it should "find sub types for annotated.Animal" in {
    val introspector = new ScalaReflectionAnnotationIntrospector
    val mapper = JsonMapper.builder().build()
    val animalType = mapper.constructType(classOf[annotated.Animal])
    val annotatedAnimalType = AnnotatedClassResolver.resolve(
      mapper.getDeserializationConfig, animalType, mapper.getDeserializationConfig)
    val subtypes = introspector.findSubtypes(annotatedAnimalType).asScala.toSeq.map(_.getType)
    subtypes should have size 2
    subtypes(0) shouldEqual classOf[annotated.Dog]
    subtypes(1) shouldEqual classOf[annotated.Cat]
  }

  it should "return version" in {
    val introspector = new ScalaReflectionAnnotationIntrospector
    introspector.version() shouldEqual JacksonModule.version
  }
}
