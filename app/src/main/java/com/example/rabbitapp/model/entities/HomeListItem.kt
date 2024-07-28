package com.example.rabbitapp.model.entities

interface HomeListItem {
    val id: Long
    val name: String
    val birth: Long
    val cageNumber: Int?
    val imagePath: String?
    val fkMother: Long?
    val fkFather: Long?
    val deathDate: Long?
}