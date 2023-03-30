package com.example.rabbitapp.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Litter(
    @PrimaryKey override val id: Long,
    override val name: String,
    override val birth: Long,
    val size: Int
) : HomeListItem
