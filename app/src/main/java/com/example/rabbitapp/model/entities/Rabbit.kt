package com.example.rabbitapp.model.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Rabbit::class,
            parentColumns = ["id"],
            childColumns = ["FkMother"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Rabbit::class,
            parentColumns = ["id"],
            childColumns = ["FkFather"],
            onDelete = ForeignKey.SET_NULL
        )]
)
data class Rabbit(
    @PrimaryKey(autoGenerate = true) override val id: Long,
    override val name: String,
    override val birth: Long,
    val sex: String,
    val earNumber: String,

    val FkMother: Long?,
    val FkFather: Long?
) : HomeListItem
