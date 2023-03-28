package com.example.rabbitapp.model.entities

import java.util.*

data class Rabbit(
    override val name: String,
    override val birth: Date,
    val sex: String,
) : HomeListItem
