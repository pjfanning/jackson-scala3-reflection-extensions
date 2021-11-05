package com.github.pjfanning.jackson.reflection

case class SeqOptionLong(values: Seq[Option[Long]])
case class WrappedSeqOptionLong(text: String, wrappedLongs: SeqOptionLong)
