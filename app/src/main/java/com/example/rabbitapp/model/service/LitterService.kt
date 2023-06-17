package com.example.rabbitapp.model.service

import androidx.lifecycle.LiveData
import com.example.rabbitapp.model.entities.Litter
import com.example.rabbitapp.model.repository.LitterRepository

class LitterService(private val litterRepository: LitterRepository) {

    fun getAll(): LiveData<List<Litter>> {
        return litterRepository.getAll()
    }

}