package com.example.rabbitapp.model.entities.relations

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.rabbitapp.model.entities.Litter
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.model.entities.Vaccine

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Rabbit::class,
            parentColumns = ["id"],
            childColumns = ["fkItem"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Litter::class,
            parentColumns = ["id"],
            childColumns = ["fkItem"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Vaccine::class,
            parentColumns = ["id"],
            childColumns = ["fkVaccine"],
            onDelete = ForeignKey.CASCADE
        )]
)
data class Vaccinated(
    @PrimaryKey val id: Long,
    val date: Long,
    val dose: String,
    var fkItem: Long,
    var fkVaccine: Long
)
