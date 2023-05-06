package com.example.rabbitapp.model.service

import android.util.Log
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.model.repository.RabbitRepository

class RabbitService(private val rabbitRepository: RabbitRepository) {

    fun getAll(): List<Rabbit> {
        return rabbitRepository.getAll()
    }

    fun save(rabbit: Rabbit): Long {
        Log.d("DATABASE", "Rabbit $rabbit saved")
        return rabbitRepository.insert(rabbit)
    }

}