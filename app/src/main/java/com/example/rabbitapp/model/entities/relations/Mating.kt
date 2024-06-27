package com.example.rabbitapp.model.entities.relations

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.rabbitapp.model.entities.Litter
import com.example.rabbitapp.model.entities.Rabbit

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Rabbit::class,
            parentColumns = ["id"],
            childColumns = ["fkMother"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Rabbit::class,
            parentColumns = ["id"],
            childColumns = ["fkFather"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Litter::class,
            parentColumns = ["id"],
            childColumns = ["fkLitter"],
            onDelete = ForeignKey.CASCADE
        )]
)
data class Mating(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val matingDate: Long,
    val birthDate: Long,
    var fkMother: Long?,
    var fkFather: Long?,
    var fkLitter: Long?
)
