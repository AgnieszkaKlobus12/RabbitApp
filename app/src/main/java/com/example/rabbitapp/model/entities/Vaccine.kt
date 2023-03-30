package com.example.rabbitapp.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Vaccine(
    @PrimaryKey val id: Long,
    val disease: String,
    val vaccineName: String
)
