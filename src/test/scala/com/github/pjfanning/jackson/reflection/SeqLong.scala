package com.github.pjfanning.jackson.reflection

case class SeqLong(longs: Seq[Long])
case class WrappedSeqLong(text: String, wrappedLongs: SeqLong)
