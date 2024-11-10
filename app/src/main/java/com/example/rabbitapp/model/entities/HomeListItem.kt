package com.example.rabbitapp.model.entities

interface HomeListItem {
    val id: Long
    var name: String
    var birth: Long
    var cageNumber: Int?
    var imagePath: String?
    var fkMother: Long?
    var fkFather: Long?
    var deathDate: Long?
    val type: String
    var earNumber: String?
}