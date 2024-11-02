package com.example.rabbitapp.model.entities.relations



data class Mating(
    val id: Long,
    val matingDate: Long,
    val birthDate: Long,
    val archived: Boolean,
    var fkMother: Long?,
    var fkFather: Long?,
    var fkLitter: Long?
)
