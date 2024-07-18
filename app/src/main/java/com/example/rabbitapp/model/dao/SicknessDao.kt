package com.example.rabbitapp.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rabbitapp.model.entities.Sickness

@Dao
interface SicknessDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sickness: Sickness): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(sickness: Sickness)

    @Query("delete from Sickness where id = :id")
    fun delete(id: Long)

    @Query("delete from Sickness")
    fun deleteAll()

    @Query("select * from Sickness")
    fun getAll(): List<Sickness>

    @Query("select * from Sickness where id = :id")
    fun getSicknessFromId(id: Long): Sickness?
}