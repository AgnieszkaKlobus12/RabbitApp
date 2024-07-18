package com.example.rabbitapp.model.entities.relations

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.rabbitapp.model.entities.Litter
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.model.entities.Sickness

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Rabbit::class,
            parentColumns = ["id"],
            childColumns = ["fkRabbit"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Litter::class,
            parentColumns = ["id"],
            childColumns = ["fkLitter"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Sickness::class,
            parentColumns = ["id"],
            childColumns = ["fkSickness"],
            onDelete = ForeignKey.CASCADE
        )]
)
data class Sick(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val startDate: Long,
    val endDate: Long,
    var fkRabbit: Long?,
    var fkLitter: Long?,
    var fkSickness: Long
)
