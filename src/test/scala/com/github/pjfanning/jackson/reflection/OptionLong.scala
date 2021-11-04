package com.github.pjfanning.jackson.reflection

case class OptionLong(valueLong: Option[Long])
case class WrappedOptionLong(text: String, wrappedLong: OptionLong)
