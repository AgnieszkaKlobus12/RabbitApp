package com.example.rabbitapp.model.service

import com.example.rabbitapp.model.dao.SickDao
import com.example.rabbitapp.model.dao.SicknessDao
import com.example.rabbitapp.model.entities.Sickness

class SicknessService(private val sickDao: SickDao, private val sicknessDao: SicknessDao) {

    fun getAllSicknesses(): List<Sickness> {
        return sicknessDao.getAll();
    }

    fun getSicknessFromId(sicknessId: Long): Sickness? {
        return sicknessDao.getSicknessFromId(sicknessId)
    }

    fun save(sickness: Sickness): Long {
        return sicknessDao.insert(sickness)
    }

    fun deleteWithId(id: Long) {
        sicknessDao.delete(id)
    }


}