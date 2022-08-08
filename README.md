![Build Status](https://github.com/pjfanning/jackson-scala3-reflection-extensions/actions/workflows/ci.yml/badge.svg?branch=main)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.pjfanning/jackson-scala3-reflection-extensions_3/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.pjfanning/jackson-scala3-reflection-extensions_3)

# jackson-scala3-reflection-extensions

Jackson Scala 3 support that uses [gzoller/scala-reflection](https://github.com/gzoller/scala-reflection)
to get type info based on Scala 3 [Tasty](https://docs.scala-lang.org/scala3/guides/tasty-overview.html) files
(or at compile time, see Performance section).

The problem that this lib solves in described in this [FAQ entry](https://github.com/FasterXML/jackson-module-scala/wiki/FAQ#deserializing-optionint-seqint-and-other-primitive-challenges).

This lib is designed to used with [jackson-module-scala](https://github.com/FasterXML/jackson-module-scala). By default,
jackson-module-scala uses Java reflection to work out the class structure.

`ScalaReflectionExtensions` can be mixed into your ObjectMapper in as a similar way to jackson-module-scala's
[ClassTagExtensions](https://github.com/FasterXML/jackson-module-scala/blob/2.14/src/main/scala/com/fasterxml/jackson/module/scala/ClassTagExtensions.scala)
and [ScalaObjectMapper](https://github.com/FasterXML/jackson-module-scala/blob/2.14/src/main/scala-2.%2B/com/fasterxml/jackson/module/scala/ScalaObjectMapper.scala).

```scala
libraryDependencies += "com.github.pjfanning" %% "jackson-scala3-reflection-extensions" % "2.13.2"
```

```scala
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.github.pjfanning.jackson.reflection.ScalaReflectionExtensions

val mapperBuilder = JsonMapper.builder()
  .addModule(DefaultScalaModule)

val mapper = mapperBuilder.build() :: ScalaReflectionExtensions

// this should also work but Jackson is moving to supporting only creating mapper instances from a builder
val mapper2 = new ObjectMapper with ScalaReflectionExtensions
mapper2.registerModule(DefaultScalaModule)

val instance = mapper.readValue[MyClass](jsonText)
```

## Known Issues
* Currently does not work with inner classes, (eg case classes defined inside Scala Objects) - see https://github.com/gzoller/scala-reflection/issues/40

## Performance

The code to calculate the class details can be slow, as detailed in [gzoller/scala-reflection](https://github.com/gzoller/scala-reflection).
The results are cached, so they won't be recalculated every time you call `readValue`.

If performance worries you then you should consider enabling the compiler plugin.

```scala
addCompilerPlugin("co.blocke" %% "scala-reflection" % "1.1.4")
```
