package com.example.rabbitapp.model.entities.relations


data class Vaccinated(
    val id: Long,
    val date: Long,
    val nextDoseDate: Long?,
    val dose: String,
    val doseNumber: Int,
    var fkRabbit: Long?,
    var fkLitter: Long?,
    var fkVaccine: Long
)
