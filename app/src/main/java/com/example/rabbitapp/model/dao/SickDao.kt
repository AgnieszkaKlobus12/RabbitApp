package com.example.rabbitapp.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rabbitapp.model.entities.relations.Sick

@Dao
interface SickDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sick: Sick): Long

    @Query("delete from Sick where id = :id")
    fun delete(id: Long)

    @Query("delete from Sick")
    fun deleteAll()

    @Query("select * from Sick")
    fun getAll(): List<Sick>

    @Query("select * from Sick where id = :id")
    fun getSickFromId(id: Long): Sick?

    @Query("select * from Sick where fkRabbit = :fkRabbit")
    fun getAllSickForRabbit(fkRabbit: Long): List<Sick>

    @Query("select * from Sick where fkLitter = :fkLitter")
    fun getAllSickForLitter(fkLitter: Long): List<Sick>

    @Query("select fkRabbit from Sick where fkSickness = :id and fkRabbit not null")
    fun getAllRabbitsWithSickness(id: Long): List<Long>

    @Query("select fkLitter from Sick where fkSickness = :id and fkLitter not null")
    fun getAllLittersWithSickness(id: Long): List<Long>
}