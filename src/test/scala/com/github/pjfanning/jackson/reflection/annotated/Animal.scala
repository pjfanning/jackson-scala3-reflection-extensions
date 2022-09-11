package com.github.pjfanning.jackson.reflection.annotated

import com.fasterxml.jackson.annotation.JsonTypeInfo

// cannot use sealed class due to https://github.com/gzoller/scala-reflection/issues/42
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
sealed trait Animal {
  val name: String
  val animalType: String = "Animal"
}

class Dog(val name: String) extends Animal {
  override val animalType: String = "Dog"
}

class Cat(val name: String) extends Animal {
  override val animalType: String = "Cat"
}

case class PetOwner(owner: String, pet: Animal)
