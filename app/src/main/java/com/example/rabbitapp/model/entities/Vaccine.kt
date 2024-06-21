package com.example.rabbitapp.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Vaccine(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val description: String
)
