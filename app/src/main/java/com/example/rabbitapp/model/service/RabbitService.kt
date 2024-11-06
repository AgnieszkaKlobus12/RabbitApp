package com.example.rabbitapp.model.service

import android.util.Log
import com.example.rabbitapp.model.dao.RabbitDao
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.utils.Gender
import com.example.rabbitapp.utils.GoogleDriveClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RabbitService(
    private val rabbitDao: RabbitDao,
    private val googleDriveClient: GoogleDriveClient
) {

    fun getAll(): List<Rabbit> {
        return rabbitDao.getAll()
    }

    fun getRabbitFromId(id: Long): Rabbit? {
        return rabbitDao.getRabbitFromId(id)
    }

    fun deleteRabbitWithId(id: Long) {
        rabbitDao.delete(id)
        CoroutineScope(Dispatchers.Default).launch {
            googleDriveClient.uploadLocalDatabase()
        }
    }

    fun getAllWithGenderExcept(gender: Gender, id: List<Long>): List<Rabbit> {
        return rabbitDao.getAllWithGenderExcept(gender.name, id)
    }

    fun save(rabbit: Rabbit): Long {
        val result = rabbitDao.insert(rabbit)
        CoroutineScope(Dispatchers.Default).launch {
            googleDriveClient.uploadLocalDatabase()
            Log.d("DATABASE", "Rabbit $rabbit saved")
        }
        return result
    }

    fun update(rabbit: Rabbit) {
        Log.d("DATABASE", "Rabbit $rabbit updated")
        val result = rabbitDao.update(rabbit)
        CoroutineScope(Dispatchers.Default).launch {
            googleDriveClient.uploadLocalDatabase()
        }
        return result
    }

    fun getAllFromLitter(id: Long): List<Rabbit> {
        return rabbitDao.getAllFromLitter(id)
    }

    fun markRabbitAsDead(rabbitId: Long, date: Long) {
        val rabbit = rabbitDao.getRabbitFromId(rabbitId)
        if (rabbit != null) {
            rabbitDao.update(rabbit.copy(deathDate = date))
        }
        CoroutineScope(Dispatchers.Default).launch {
            googleDriveClient.uploadLocalDatabase()
        }
    }

}