package com.example.rabbitapp.model.service

import android.util.Log
import com.example.rabbitapp.model.entities.Litter
import com.example.rabbitapp.model.dao.LitterDao

class LitterService(private val litterDao: LitterDao) {

    fun getAll(): List<Litter> {
        return litterDao.getAll()
    }

    fun save(litter: Litter): Long {
        Log.d("DATABASE", "Litter $litter saved")
        return litterDao.insert(litter)
    }

    fun deleteWithId(id: Long) {
        litterDao.deleteWithId(id)
    }
}