package com.example.rabbitapp.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rabbitapp.model.entities.Rabbit

@Dao
interface RabbitDao {

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

    @Query("select * from Rabbit where sex = :gender and id not in (:ids)")
    fun getAllWithGenderExcept(gender: String, ids: List<Long>): List<Rabbit>

    @Query("select * from Rabbit where sex = :gender")
    fun getAllWithGender(gender: String): List<Rabbit>

    @Query("select * from Rabbit where id = :id")
    fun getRabbitFromId(id: Long): Rabbit?

    @Query("select * from Rabbit where fkLitter = :id")
    fun getAllFromLitter(id: Long): List<Rabbit>
}