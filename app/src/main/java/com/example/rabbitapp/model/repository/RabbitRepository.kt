package com.example.rabbitapp.model.repository

import androidx.room.*
import com.example.rabbitapp.model.entities.Rabbit

@Dao
interface RabbitRepository {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(rabbit: Rabbit): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(rabbit: Rabbit)

    @Query("delete from Rabbit where id = :id")
    fun delete(id: Long)

    @Query("delete from Rabbit")
    fun deleteAll()

    @Query("select * from Rabbit")
    fun getAll(): List<Rabbit>

    @Query("select * from Rabbit where sex = :gender")
    fun getAllWithGender(gender: String): List<Rabbit>

    @Query("select * from Rabbit where id = :id")
    fun getRabbitFromId(id: Long): Rabbit?
}