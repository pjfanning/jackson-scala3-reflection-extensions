package com.github.pjfanning.jackson.reflection

case class OptionLong(valueLong: Option[Long])
case class OptionLongWithDefault(valueLong: Option[Long] = None)
case class WrappedOptionLong(text: String, wrappedLong: OptionLong)
case class WrappedOptionLongWithDefault(text: String, wrappedLong: OptionLongWithDefault)

final case class OptionVarLong(var valueLong: Option[Long])
final case class WrappedOptionVarLong(text: String, var wrappedLong: OptionVarLong)
final case class WrappedOptionOptionVarLong(text: String, var wrappedLong: Option[OptionVarLong])
