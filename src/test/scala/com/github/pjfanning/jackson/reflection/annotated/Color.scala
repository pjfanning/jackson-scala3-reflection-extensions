package com.github.pjfanning.jackson.reflection.annotated

import com.fasterxml.jackson.annotation.JsonTypeInfo

case class Car(make: String, color: Color)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
sealed trait Color

case object Red extends Color
case object Green extends Color
case object Blue extends Color
