package com.example.rabbitapp.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Rabbit(
    @PrimaryKey override val id: Long,
    override val name: String,
    override val birth: Long,
    val sex: String,
) : HomeListItem
