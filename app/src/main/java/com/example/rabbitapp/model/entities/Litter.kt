package com.example.rabbitapp.model.entities

import java.util.*

data class Litter(
    override val name: String,
    override val birth: Date,
    val size: Int
) : HomeListItem
