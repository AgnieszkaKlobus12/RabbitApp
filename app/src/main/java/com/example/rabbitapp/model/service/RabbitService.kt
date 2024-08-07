package com.example.rabbitapp.model.service

import android.util.Log
import com.example.rabbitapp.model.dao.RabbitDao
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.utils.Gender

class RabbitService(private val rabbitDao: RabbitDao) {

    fun getAll(): List<Rabbit> {
        return rabbitDao.getAll()
    }

    fun getRabbitFromId(id: Long): Rabbit? {
        return rabbitDao.getRabbitFromId(id)
    }

    fun deleteRabbitWithId(id: Long) {
        rabbitDao.delete(id)
    }

    fun getAllWithGenderExcept(gender: Gender, id: List<Long>): List<Rabbit> {
        return rabbitDao.getAllWithGenderExcept(gender.name, id)
    }

    fun save(rabbit: Rabbit): Long {
        Log.d("DATABASE", "Rabbit $rabbit saved")
        return rabbitDao.insert(rabbit)
    }

    fun update(rabbit: Rabbit) {
        Log.d("DATABASE", "Rabbit $rabbit updated")
        return rabbitDao.update(rabbit)
    }

    fun getAllFromLitter(id: Long): List<Rabbit> {
        return rabbitDao.getAllFromLitter(id)
    }

    fun markRabbitAsDead(rabbitId: Long, date: Long) {
        val rabbit = rabbitDao.getRabbitFromId(rabbitId)
        if (rabbit != null) {
            rabbitDao.update(rabbit.copy(deathDate = date))
        }
    }

}