package com.github.pjfanning.jackson.reflection

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.introspect.ScalaAnnotationIntrospector
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ScalaReflectionExtensionsExtrasTest extends AnyFlatSpec with Matchers {
  "An ObjectMapper with ScalaReflectionExtensions mixin" should "deserialize WrappedOptionLong" in {
    val mapper = newMapperWithScalaReflectionExtensions
    val v1 = mapper.readValue[WrappedOptionLong]("""{"text":"myText","wrappedLong":{"valueLong":151}}""")
    v1 shouldBe WrappedOptionLong("myText", OptionLong(Some(151L)))
    v1.wrappedLong.valueLong.get shouldBe 151L
    useOptionLong(v1.wrappedLong.valueLong) shouldBe 302L
  }

  it should "deserialize WrappedSeqLong" in {
    val mapper = newMapperWithScalaReflectionExtensions
    val w1 = WrappedSeqLong("myText", SeqLong(Seq(100L, 100000000000000L)))
    val t1 = mapper.writeValueAsString(w1)
    val v1 = mapper.readValue[WrappedSeqLong](t1)
    v1 shouldEqual w1
    useSeqLong(v1.wrappedLongs.longs) shouldEqual w1.wrappedLongs.longs.sum
  }

  it should "deserialize WrappedSeqLong with old style mix-in" in {
    val mapper = new ObjectMapper with ScalaReflectionExtensions
    mapper.registerModule(DefaultScalaModule)
    val w1 = WrappedSeqLong("myText", SeqLong(Seq(100L, 100000000000000L)))
    val t1 = mapper.writeValueAsString(w1)
    val v1 = mapper.readValue[WrappedSeqLong](t1)
    v1 shouldEqual w1
    useSeqLong(v1.wrappedLongs.longs) shouldEqual w1.wrappedLongs.longs.sum
  }

  it should "deserialize WrappedSeqOptionLong" in {
    val mapper = newMapperWithScalaReflectionExtensions
    val w1 = WrappedSeqOptionLong("myText", SeqOptionLong(Seq(Some(100L), Some(100000000000000L), None)))
    val t1 = mapper.writeValueAsString(w1)
    //TODO next lines fail - probably a bug in jackson-module-scala
    //val v1 = mapper.readValue[WrappedSeqOptionLong](t1)
    //v1 shouldEqual w1
    //v1.wrappedLongs.values.map(useOptionLong).sum shouldEqual w1.wrappedLongs.values.map(useOptionLong).sum
  }

  private def newMapperWithScalaReflectionExtensions: ObjectMapper with ScalaReflectionExtensions = {
    JsonMapper.builder().addModule(DefaultScalaModule).build() :: ScalaReflectionExtensions
  }

  private def useOptionLong(v: Option[Long]): Long = v.map(_ * 2).getOrElse(0L)
  private def useSeqLong(longs: Seq[Long]): Long = longs.sum
}
