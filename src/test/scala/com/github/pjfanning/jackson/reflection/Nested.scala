package com.github.pjfanning.jackson.reflection

object Nested {
  case class OptionLong(valueLong: Option[Long])
  case class OptionSeqLong(values: Option[Seq[Long]])
}
