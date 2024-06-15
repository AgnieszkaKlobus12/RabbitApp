package com.example.rabbitapp.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Vaccine(
    @PrimaryKey val id: Long,
    val name: String,
    val description: String
)
