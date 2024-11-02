package com.example.rabbitapp.model.entities.relations


data class Sick(
    val id: Long,
    val startDate: Long,
    val endDate: Long?,
    val description: String,
    var fkRabbit: Long?,
    var fkLitter: Long?,
    var fkSickness: Long
)
