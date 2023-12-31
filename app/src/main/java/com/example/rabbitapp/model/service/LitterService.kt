package com.example.rabbitapp.model.service

import androidx.lifecycle.LiveData
import com.example.rabbitapp.model.dao.LitterDao
import com.example.rabbitapp.model.entities.Litter
import com.example.rabbitapp.model.repository.LitterRepository

class LitterService(private val litterDao: LitterDao) {

    fun getAll(): List<Litter> {
        return litterDao.getAll()
    }

    fun deleteWithId(it: Long) {
        litterDao.deleteWithId(it);
    }

    fun save(litter: Litter) {
        litterDao.insert(litter)
    }

}