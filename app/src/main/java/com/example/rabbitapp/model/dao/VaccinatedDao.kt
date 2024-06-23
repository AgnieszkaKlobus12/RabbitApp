package com.example.rabbitapp.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rabbitapp.model.entities.relations.Vaccinated

@Dao
interface VaccinatedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vaccinated: Vaccinated): Long

    @Query("delete from Vaccinated where id = :id")
    fun delete(id: Long)

    @Query("delete from Vaccinated")
    fun deleteAll()

    @Query("select * from Vaccinated")
    fun getAll(): List<Vaccinated>

    @Query("select * from Vaccinated where id = :id")
    fun getVaccinatedFromId(id: Long): Vaccinated?

    @Query("select * from Vaccinated where fkRabbit = :fkRabbit")
    fun getAllVaccinationsForRabbit(fkRabbit: Long): List<Vaccinated>

    @Query("select * from Vaccinated where fkLitter = :fkLitter")
    fun getAllVaccinationsForLitter(fkLitter: Long): List<Vaccinated>
}