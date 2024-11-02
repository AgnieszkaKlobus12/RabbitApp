package com.example.rabbitapp.model.entities

data class Litter(
    override val id: Long,
    override val name: String,
    override val birth: Long,
    val size: Int,
    override val cageNumber: Int?,
    override val imagePath: String?,
    override val fkMother: Long?,
    override val fkFather: Long?,
    override var deathDate: Long?
) : HomeListItem {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Litter

        if (id != other.id) return false
        if (name != other.name) return false
        if (birth != other.birth) return false
        if (size != other.size) return false
        if (fkMother != other.fkMother) return false
        if (fkFather != other.fkFather) return false
        if (cageNumber != other.cageNumber) return false
        if (deathDate != other.deathDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + birth.hashCode()
        result = 31 * result + size
        result = 31 * result + (fkMother?.hashCode() ?: 0)
        result = 31 * result + (fkFather?.hashCode() ?: 0)
        result = 31 * result + (cageNumber ?: 0)
        result = 31 * result + (deathDate?.hashCode() ?: 0)
        return result
    }
}
