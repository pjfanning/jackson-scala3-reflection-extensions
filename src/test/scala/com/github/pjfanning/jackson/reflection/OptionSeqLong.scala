package com.github.pjfanning.jackson.reflection

case class OptionSeqLong(values: Option[Seq[Long]])
case class WrappedOptionSeqLong(text: String, wrappedLongs: OptionSeqLong)
