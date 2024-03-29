package com.github.pjfanning.jackson.reflection

import co.blocke.scala_reflection.RType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.introspect.{ScalaAnnotationIntrospector, ScalaAnnotationIntrospectorModule}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ScalaReflectionExtensionsExtrasTest extends AnyFlatSpec with Matchers with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    ScalaAnnotationIntrospectorModule.clearRegisteredReferencedTypes()
  }

  override def afterEach(): Unit = {
    ScalaAnnotationIntrospectorModule.clearRegisteredReferencedTypes()
  }

  "An ObjectMapper with ScalaReflectionExtensions mixin" should "deserialize WrappedOptionLong" in {
    val mapper = newMapperWithScalaReflectionExtensions
    val v1 = mapper.readValue[WrappedOptionLong]("""{"text":"myText","wrappedLong":{"valueLong":151}}""")
    v1 shouldBe WrappedOptionLong("myText", OptionLong(Some(151L)))
    v1.wrappedLong.valueLong.get shouldBe 151L
    useOptionLong(v1.wrappedLong.valueLong) shouldBe 302L
  }

  it should "deserialize WrappedOptionLongWithDefault" in {
    val mapper = newMapperWithScalaReflectionExtensions
    val v1 = mapper.readValue[WrappedOptionLongWithDefault]("""{"text":"myText","wrappedLong":{"valueLong":151}}""")
    v1 shouldBe WrappedOptionLongWithDefault("myText", OptionLongWithDefault(Some(151L)))
    v1.wrappedLong.valueLong.get shouldBe 151L
    useOptionLong(v1.wrappedLong.valueLong) shouldBe 302L
  }

  it should "deserialize WrappedOptionVarLong" in {
    val mapper = newMapperWithScalaReflectionExtensions
    val v1 = mapper.readValue[WrappedOptionVarLong]("""{"text":"myText","wrappedLong":{"valueLong":151}}""")
    v1 shouldBe WrappedOptionVarLong("myText", OptionVarLong(Some(151L)))
    v1.wrappedLong.valueLong.get shouldBe 151L
    useOptionLong(v1.wrappedLong.valueLong) shouldBe 302L
  }

  it should "deserialize WrappedOptionOptionVarLong" in {
    val mapper = newMapperWithScalaReflectionExtensions
    val v1 = mapper.readValue[WrappedOptionOptionVarLong]("""{"text":"myText","wrappedLong":{"valueLong":151}}""")
    v1 shouldBe WrappedOptionOptionVarLong("myText", Some(OptionVarLong(Some(151L))))
    v1.wrappedLong shouldBe defined
    v1.wrappedLong.get.valueLong.get shouldBe 151L
    useOptionLong(v1.wrappedLong.get.valueLong) shouldBe 302L
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
    val v1 = mapper.readValue[WrappedSeqOptionLong](t1)
    v1 shouldEqual w1
    v1.wrappedLongs.values.map(useOptionLong).sum shouldEqual w1.wrappedLongs.values.map(useOptionLong).sum
  }

  it should "deserialize WrappedOptionSeqLong" in {
    val mapper = newMapperWithScalaReflectionExtensions
    val w1 = WrappedOptionSeqLong("myText", OptionSeqLong(Some(Seq(100L, 100000000000000L))))
    val t1 = mapper.writeValueAsString(w1)
    val v1 = mapper.readValue[WrappedOptionSeqLong](t1)
    v1 shouldEqual w1
    v1.wrappedLongs.values.getOrElse(Seq.empty).sum shouldEqual w1.wrappedLongs.values.getOrElse(Seq.empty).sum
  }

  it should "deserialize OptionSeqOptionLong" in {
    val mapper = newMapperWithScalaReflectionExtensions
    val w1 = OptionSeqOptionLong(Some(Seq(Some(100L), None)))
    val t1 = mapper.writeValueAsString(w1)
    val v1 = mapper.readValue[OptionSeqOptionLong](t1)
    v1 shouldEqual w1
    v1.values.get.flatten.sum shouldEqual w1.values.get.flatten.sum
  }

  it should "deserialize Nested.OptionLong (with RType)" in {
    val mapper = newMapperWithScalaReflectionExtensions
    val w1 = Nested.OptionLong(Some(1000L))
    val t1 = mapper.writeValueAsString(w1)
    val v1 = mapper.readValue[Nested.OptionLong](t1, RType.of[Nested.OptionLong])
    v1 shouldEqual w1
    useOptionLong(v1.valueLong) shouldEqual useOptionLong(w1.valueLong)
  }

  it should "deserialize Nested.OptionSeqLong (with RType)" in {
    val mapper = newMapperWithScalaReflectionExtensions
    val w1 = Nested.OptionSeqLong(Some(Seq(1000L, 123L)))
    val t1 = mapper.writeValueAsString(w1)
    val v1 = mapper.readValue[Nested.OptionSeqLong](t1)
    v1 shouldEqual w1
    v1.values.get.sum shouldEqual w1.values.get.sum
  }

  it should "deserialize DataExampleClass" in {
    val mapper = newMapperWithScalaReflectionExtensions
    val w1 = DataExampleClass(CustomCollection(Seq(FeatureExample(Some(ExampleProperties(1, 1.23))))))
    val t1 = mapper.writeValueAsString(w1)
    val v1 = mapper.readValue[DataExampleClass](t1)
    v1 shouldEqual w1
  }

  it should "deserialize Nested.OptionLong" in {
    val mapper = newMapperWithScalaReflectionExtensions
    val w1 = Nested.OptionLong(Some(1000L))
    val t1 = mapper.writeValueAsString(w1)
    val v1 = mapper.readValue[Nested.OptionLong](t1)
    v1 shouldEqual w1
    useOptionLong(v1.valueLong) shouldEqual useOptionLong(w1.valueLong)
  }

  it should "deserialize Nested.OptionSeqLong" in {
    val mapper = newMapperWithScalaReflectionExtensions
    val w1 = Nested.OptionSeqLong(Some(Seq(1000L, 123L)))
    val t1 = mapper.writeValueAsString(w1)
    val v1 = mapper.readValue[Nested.OptionSeqLong](t1)
    v1 shouldEqual w1
    v1.values.get.sum shouldEqual w1.values.get.sum
  }

  private def newMapperWithScalaReflectionExtensions: ObjectMapper with ScalaReflectionExtensions = {
    JsonMapper.builder().addModule(DefaultScalaModule).build() :: ScalaReflectionExtensions
  }

  private def useOptionLong(v: Option[Long]): Long = v.map(_ * 2).getOrElse(0L)
  private def useSeqLong(longs: Seq[Long]): Long = longs.sum
}
