package com.example.rabbitapp.model.repository

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.rabbitapp.model.entities.Litter
import com.example.rabbitapp.model.entities.Rabbit

@Dao
interface LitterRepository {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(litter: Litter): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(litter: Litter)

//    @Query("select * from Rabbit where nrLegitymacji = :leaderId")
//    fun getLeader(rabbit: Int): List<Rabbit>

    @Delete
    fun delete(litter: Litter)

    @Query("delete from Litter")
    fun deleteAll()

    @Query("select * from Litter")
    fun getAll(): LiveData<List<Litter>>

}