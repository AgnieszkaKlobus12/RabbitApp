package com.example.rabbitapp.model.entities

data class Rabbit(
    override val id: Long,
    override val name: String,
    override var birth: Long,
    val sex: String,
    val earNumber: String?,
    override val cageNumber: Int?,
    override val imagePath: String?,
    override var fkMother: Long?,
    override var fkFather: Long?,
    var fkLitter: Long?,
    override var deathDate: Long?
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
        if (deathDate != other.deathDate) return false
        if (cageNumber != other.cageNumber) return false

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
        result = 31 * result + (fkLitter?.hashCode() ?: 0)
        result = 31 * result + (deathDate?.hashCode() ?: 0)
        result = 31 * result + (cageNumber ?: 0)
        return result
    }
}
