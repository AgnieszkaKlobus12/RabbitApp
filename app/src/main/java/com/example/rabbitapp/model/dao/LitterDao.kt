package com.example.rabbitapp.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rabbitapp.model.entities.Litter

@Dao
interface LitterDao {

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

    @Query("select * from Litter where id = :id")
    fun getLitterFromId(id: Long): Litter?

}