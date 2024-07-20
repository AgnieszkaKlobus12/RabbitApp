package com.example.rabbitapp.model.service

import com.example.rabbitapp.model.dao.SickDao
import com.example.rabbitapp.model.dao.SicknessDao
import com.example.rabbitapp.model.entities.Sickness
import com.example.rabbitapp.model.entities.relations.Sick

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

    fun save(sickness: Sick): Long {
        return sickDao.insert(sickness)
    }

    fun getAllSickForRabbit(it: Long): List<Sick> {
        return sickDao.getAllSickForRabbit(it)
    }

    fun getAllSickForLitter(it: Long): List<Sick> {
        return sickDao.getAllSickForLitter(it)
    }

    fun getSickFromId(sickId: Long): Sick? {
        return sickDao.getSickFromId(sickId)
    }

    fun deleteSickWithId(id: Long) {
        sickDao.delete(id)
    }

    fun getAllRabbitsWithSickness(id: Long): List<Long> {
        return sickDao.getAllRabbitsWithSickness(id);
    }

    fun getAllLittersWithSickness(id: Long): List<Long> {
        return sickDao.getAllLittersWithSickness(id);
    }

}