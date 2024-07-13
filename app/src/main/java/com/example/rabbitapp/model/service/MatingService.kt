package com.example.rabbitapp.model.service

import com.example.rabbitapp.model.dao.MatingDao
import com.example.rabbitapp.model.entities.relations.Mating

class MatingService(private val matingDao: MatingDao) {

    fun getAll(): List<Mating> {
        return matingDao.getAll()
    }

    fun deleteWithId(it: Long) {
        matingDao.deleteWithId(it);
    }

    fun save(mating: Mating): Long {
        return matingDao.insert(mating)
    }

    fun getMatingFromId(id: Long): Mating? {
        return matingDao.getMatingFromId(id)
    }

    fun getAllNotArchived(): List<Mating> {
        return matingDao.getAllNotArchived()
    }

}