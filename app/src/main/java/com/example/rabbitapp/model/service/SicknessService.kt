package com.example.rabbitapp.model.service

import com.example.rabbitapp.model.dao.SickDao
import com.example.rabbitapp.model.dao.SicknessDao
import com.example.rabbitapp.model.entities.Sickness
import com.example.rabbitapp.model.entities.relations.Sick
import com.example.rabbitapp.utils.GoogleDriveClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SicknessService(
    private val sickDao: SickDao, private val sicknessDao: SicknessDao,
    private val googleDriveClient: GoogleDriveClient
) {

    fun getAllSicknesses(): List<Sickness> {
        return sicknessDao.getAll();
    }

    fun getSicknessFromId(sicknessId: Long): Sickness? {
        return sicknessDao.getSicknessFromId(sicknessId)
    }

    fun save(sickness: Sickness): Long {
        val result = sicknessDao.insert(sickness)
        CoroutineScope(Dispatchers.Default).launch {
            googleDriveClient.uploadLocalDatabase()
        }
        return result
    }

    fun deleteWithId(id: Long) {
        sicknessDao.delete(id)
        CoroutineScope(Dispatchers.Default).launch {
            googleDriveClient.uploadLocalDatabase()
        }
    }

    fun save(sickness: Sick): Long {
        val result = sickDao.insert(sickness)
        CoroutineScope(Dispatchers.Default).launch {
            googleDriveClient.uploadLocalDatabase()
        }
        return result
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
        CoroutineScope(Dispatchers.Default).launch {
            googleDriveClient.uploadLocalDatabase()
        }
    }

    fun getAllRabbitsWithSickness(id: Long): List<Long> {
        return sickDao.getAllRabbitsWithSickness(id);
    }

    fun getAllLittersWithSickness(id: Long): List<Long> {
        return sickDao.getAllLittersWithSickness(id);
    }

}