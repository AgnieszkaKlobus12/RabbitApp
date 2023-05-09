package com.example.rabbitapp.model.repository

import androidx.room.*
import com.example.rabbitapp.model.entities.Litter

@Dao
interface LitterRepository {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(litter: Litter): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(litter: Litter)

    @Query("delete from Litter where id = :id")
    fun deleteWithId(id: Long)

    @Query("delete from Litter")
    fun deleteAll()

    @Query("select * from Litter")
    fun getAll(): List<Litter>

}