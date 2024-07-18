package com.example.rabbitapp.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Sickness(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val symptoms: String,
    val treatment: String
)
