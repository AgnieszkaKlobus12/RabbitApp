package com.example.rabbitapp.model.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Rabbit::class,
            parentColumns = ["id"],
            childColumns = ["fkMother"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Rabbit::class,
            parentColumns = ["id"],
            childColumns = ["fkFather"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Litter::class,
            parentColumns = ["id"],
            childColumns = ["fkLitter"],
            onDelete = ForeignKey.SET_NULL
        )]
)
data class Rabbit(
    @PrimaryKey(autoGenerate = true) override val id: Long,
    override val name: String,
    override val birth: Long,
    val sex: String,
    val earNumber: String?,
    override val imagePath: String?,
    override val fkMother: Long?,
    override val fkFather: Long?,
    val fkLitter: Long?
) : HomeListItem {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rabbit

        if (id != other.id) return false
        if (name != other.name) return false
        if (birth != other.birth) return false
        if (sex != other.sex) return false
        if (earNumber != other.earNumber) return false
        if (fkMother != other.fkMother) return false
        if (fkFather != other.fkFather) return false
        if (fkLitter != other.fkLitter) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + birth.hashCode()
        result = 31 * result + sex.hashCode()
        result = 31 * result + earNumber.hashCode()
        result = 31 * result + (fkMother?.hashCode() ?: 0)
        result = 31 * result + (fkFather?.hashCode() ?: 0)
        return result
    }
}
