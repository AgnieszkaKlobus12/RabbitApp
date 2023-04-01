package com.example.rabbitapp.model.service

import androidx.lifecycle.LiveData
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.model.repository.RabbitRepository

class RabbitService(private val rabbitRepository: RabbitRepository) {

    fun getAll(): LiveData<List<Rabbit>> {
        return rabbitRepository.getAll()
    }


}