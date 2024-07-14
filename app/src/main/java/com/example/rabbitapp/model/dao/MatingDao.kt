package com.example.rabbitapp.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rabbitapp.model.entities.relations.Mating

@Dao
interface MatingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(mating: Mating): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(mating: Mating)

    @Query("delete from Mating where id = :id")
    fun deleteWithId(id: Long)

    @Query("delete from Mating")
    fun deleteAll()

    @Query("select * from Mating")
    fun getAll(): List<Mating>

    @Query("select * from Mating where id = :id")
    fun getMatingFromId(id: Long): Mating?

    @Query("select * from Mating where archived = 0")
    fun getAllNotArchived(): List<Mating>

    @Query("select * from Mating where fkFather = :id or fkMother = :id")
    fun getAllMatingsForRabbit(id: Long): List<Mating>

}