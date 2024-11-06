package com.example.rabbitapp.model.service

import com.example.rabbitapp.model.dao.LitterDao
import com.example.rabbitapp.model.entities.Litter
import com.example.rabbitapp.utils.GoogleDriveClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LitterService(
    private val litterDao: LitterDao,
    private val googleDriveClient: GoogleDriveClient
) {

    fun getAll(): List<Litter> {
        return litterDao.getAll()
    }

    fun deleteWithId(it: Long) {
        litterDao.deleteWithId(it)
        CoroutineScope(Dispatchers.Default).launch {
            googleDriveClient.uploadLocalDatabase()
        }
    }

    fun save(litter: Litter): Long {
        val result = litterDao.insert(litter)
        CoroutineScope(Dispatchers.Default).launch {
            googleDriveClient.uploadLocalDatabase()
        }
        return result
    }

    fun getLitterFromId(id: Long): Litter? {
        val result = litterDao.getLitterFromId(id)
        CoroutineScope(Dispatchers.Default).launch {
            googleDriveClient.uploadLocalDatabase()
        }
        return result
    }

    fun markLitterAsDead(litterId: Long, date: Long) {
        val litter = litterDao.getLitterFromId(litterId)
        if (litter != null) {
            litterDao.update(litter.copy(deathDate = date))
        }
        CoroutineScope(Dispatchers.Default).launch {
            googleDriveClient.uploadLocalDatabase()
        }
    }

}