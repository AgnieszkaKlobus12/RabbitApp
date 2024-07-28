package com.example.rabbitapp.model.service

import com.example.rabbitapp.model.dao.LitterDao
import com.example.rabbitapp.model.entities.Litter

class LitterService(private val litterDao: LitterDao) {

    fun getAll(): List<Litter> {
        return litterDao.getAll()
    }

    fun deleteWithId(it: Long) {
        litterDao.deleteWithId(it);
    }

    fun save(litter: Litter): Long {
        return litterDao.insert(litter)
    }

    fun getLitterFromId(id: Long): Litter? {
        return litterDao.getLitterFromId(id)
    }

    fun markLitterAsDead(litterId: Long, date: Long) {
        val litter = litterDao.getLitterFromId(litterId)
        if (litter != null) {
            litterDao.update(litter.copy(deathDate = date))
        }
    }

}