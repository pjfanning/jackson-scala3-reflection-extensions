package com.github.pjfanning.jackson.reflection.annotated

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
sealed abstract class Animal {
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
