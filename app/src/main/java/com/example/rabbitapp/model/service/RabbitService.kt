package com.example.rabbitapp.model.service

import android.util.Log
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.model.repository.RabbitRepository
import com.example.rabbitapp.utils.Gender

class RabbitService(private val rabbitRepository: RabbitRepository) {

    fun getAll(): List<Rabbit> {
        return rabbitRepository.getAll()
    }

    fun getRabbitFromId(id: Long): Rabbit? {
        return rabbitRepository.getRabbitFromId(id)
    }

    fun deleteRabbitWithId(id: Long) {
        rabbitRepository.delete(id)
    }

    fun getAllWithGender(gender: Gender): List<Rabbit> {
        return rabbitRepository.getAllWithGender(gender.name)
    }

    fun save(rabbit: Rabbit): Long {
        Log.d("DATABASE", "Rabbit $rabbit saved")
        return rabbitRepository.insert(rabbit)
    }

}