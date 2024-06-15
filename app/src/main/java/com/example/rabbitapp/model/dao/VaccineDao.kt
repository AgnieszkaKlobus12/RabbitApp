package com.example.rabbitapp.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rabbitapp.model.entities.Vaccine

@Dao
interface VaccineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vaccine: Vaccine): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vaccine: Vaccine)

    @Query("delete from Vaccine where id = :id")
    fun delete(id: Long)

    @Query("delete from Vaccine")
    fun deleteAll()

    @Query("select * from Vaccine")
    fun getAll(): List<Vaccine>

    @Query("select * from Vaccine where id = :id")
    fun getVaccineFromId(id: Long): Vaccine?
}