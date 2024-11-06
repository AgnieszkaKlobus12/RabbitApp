package com.example.rabbitapp.model.service

import com.example.rabbitapp.model.dao.MatingDao
import com.example.rabbitapp.model.entities.relations.Mating
import com.example.rabbitapp.utils.GoogleDriveClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MatingService(
    private val matingDao: MatingDao,
    private val googleDriveClient: GoogleDriveClient
) {

    fun getAll(): List<Mating> {
        return matingDao.getAll()
    }

    fun deleteWithId(it: Long) {
        matingDao.deleteWithId(it)
        CoroutineScope(Dispatchers.Default).launch {
            googleDriveClient.uploadLocalDatabase()
        }
    }

    fun save(mating: Mating): Long {
        val result = matingDao.insert(mating)
        CoroutineScope(Dispatchers.Default).launch {
            googleDriveClient.uploadLocalDatabase()
        }
        return result
    }

    fun getMatingFromId(id: Long): Mating? {
        return matingDao.getMatingFromId(id)
    }

    fun getAllNotArchived(): List<Mating> {
        return matingDao.getAllNotArchived()
    }

    fun getAllMatingsForRabbit(id: Long): List<Mating> {
        return matingDao.getAllMatingsForRabbit(id)
    }

}