package com.example.rabbitapp.model.service

import com.example.rabbitapp.model.dao.VaccineDao
import com.example.rabbitapp.model.entities.Vaccine

class VaccineService(private val vaccineDao: VaccineDao) {

    fun getAll(): List<Vaccine> {
        return vaccineDao.getAll()
    }

    fun getById(id: Long): Vaccine? {
        return vaccineDao.getVaccineFromId(id)
    }
}