package com.example.rabbitapp.model.repository

import androidx.room.*
import com.example.rabbitapp.model.entities.Rabbit

@Dao
interface RabbitRepository {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(rabbit: Rabbit): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(rabbit: Rabbit)
//
//    @Query("select * from Rabbit where nrLegitymacji = :leaderId")
//    fun getLeader(rabbit: Int): List<Rabbit>

    @Delete
    fun delete(rabbit: Rabbit)

    @Query("delete from Rabbit")
    fun deleteAll()

    @Query("select * from Rabbit")
    fun getAll(): List<Rabbit>

    @Query("select * from Rabbit where sex = :gender")
    fun getAllWithGender(gender: String): List<Rabbit>
}