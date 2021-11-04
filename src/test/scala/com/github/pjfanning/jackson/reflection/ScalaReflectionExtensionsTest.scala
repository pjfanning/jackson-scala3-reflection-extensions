package com.github.pjfanning.jackson.reflection

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.introspect.ScalaAnnotationIntrospector
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ScalaReflectionExtensionsTest extends AnyFlatSpec with Matchers {
  "An ObjectMapper with ScalaReflectionExtensions mixin" should "deserialize WrappedOptionLong" in {
    val mapper = newMapperWithScalaReflectionExtensions
    val v1 = mapper.readValue[WrappedOptionLong]("""{"text":"myText","wrappedLong":{"valueLong":151}}""")
    v1 shouldBe WrappedOptionLong("myText", OptionLong(Some(151L)))
    v1.wrappedLong.valueLong.get shouldBe 151L
    useOptionLong(v1.wrappedLong.valueLong) shouldBe 302L
  }

  private def newMapperWithScalaReflectionExtensions: ObjectMapper with ScalaReflectionExtensions = {
    JsonMapper.builder().addModule(DefaultScalaModule).build() :: ScalaReflectionExtensions
  }

  private def useOptionLong(v: Option[Long]): Long = v.map(_ * 2).getOrElse(0L)
}
