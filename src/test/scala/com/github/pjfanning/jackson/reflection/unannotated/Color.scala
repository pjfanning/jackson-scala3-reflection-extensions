package com.github.pjfanning.jackson.reflection.unannotated

case class Car(make: String, color: Color)

sealed trait Color

case object Red extends Color
case object Green extends Color
case object Blue extends Color
