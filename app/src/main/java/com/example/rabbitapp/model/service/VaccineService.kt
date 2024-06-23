package com.example.rabbitapp.model.service

import com.example.rabbitapp.model.dao.VaccinatedDao
import com.example.rabbitapp.model.dao.VaccineDao
import com.example.rabbitapp.model.entities.Vaccine
import com.example.rabbitapp.model.entities.relations.Vaccinated

class VaccineService(private val vaccineDao: VaccineDao, private val vaccinatedDao: VaccinatedDao) {

    fun getAll(): List<Vaccine> {
        return vaccineDao.getAll()
    }

    fun getById(id: Long): Vaccine? {
        return vaccineDao.getVaccineFromId(id)
    }

    fun save(vaccine: Vaccine): Long {
        return vaccineDao.insert(vaccine)
    }

    fun save(vaccinated: Vaccinated): Long {
        return vaccinatedDao.insert(vaccinated)
    }

    fun deleteWithId(id: Long) {
        vaccineDao.delete(id)
    }

    fun getAllVaccinationsForRabbit(fkRabbit: Long): List<Vaccinated> {
        return vaccinatedDao.getAllVaccinationsForRabbit(fkRabbit)
    }

    fun getAllVaccinationsForLitter(fkLitter: Long): List<Vaccinated> {
        return vaccinatedDao.getAllVaccinationsForLitter(fkLitter)
    }
}