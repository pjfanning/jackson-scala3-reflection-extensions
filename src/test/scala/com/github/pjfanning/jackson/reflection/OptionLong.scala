package com.github.pjfanning.jackson.reflection

case class OptionLong(valueLong: Option[Long])
case class WrappedOptionLong(text: String, wrappedLong: OptionLong)

final case class OptionVarLong(var valueLong: Option[Long])
final case class WrappedOptionVarLong(text: String, var wrappedLong: OptionVarLong)
final case class WrappedOptionOptionVarLong(text: String, var wrappedLong: Option[OptionVarLong])
