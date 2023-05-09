package com.example.rabbitapp.model.service

import android.util.Log
import com.example.rabbitapp.model.entities.Litter
import com.example.rabbitapp.model.repository.LitterRepository

class LitterService(private val litterRepository: LitterRepository) {

    fun getAll(): List<Litter> {
        return litterRepository.getAll()
    }

    fun save(litter: Litter): Long {
        Log.d("DATABASE", "Litter $litter saved")
        return litterRepository.insert(litter)
    }

    fun deleteWithId(id: Long) {
        litterRepository.deleteWithId(id)
    }
}