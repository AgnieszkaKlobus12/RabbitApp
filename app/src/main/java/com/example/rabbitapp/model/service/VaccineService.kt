package com.example.rabbitapp.model.service

import com.example.rabbitapp.model.dao.VaccinatedDao
import com.example.rabbitapp.model.dao.VaccineDao
import com.example.rabbitapp.model.entities.Vaccine
import com.example.rabbitapp.model.entities.relations.Vaccinated
import com.example.rabbitapp.utils.GoogleDriveClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VaccineService(
    private val vaccineDao: VaccineDao, private val vaccinatedDao: VaccinatedDao,
    private val googleDriveClient: GoogleDriveClient
) {

    fun getAll(): List<Vaccine> {
        return vaccineDao.getAll()
    }

    fun getById(id: Long): Vaccine? {
        return vaccineDao.getVaccineFromId(id)
    }

    fun save(vaccine: Vaccine): Long {
        val result = vaccineDao.insert(vaccine)
        CoroutineScope(Dispatchers.Default).launch {
            googleDriveClient.uploadLocalDatabase()
        }
        return result
    }

    fun save(vaccinated: Vaccinated): Long {
        val result = vaccinatedDao.insert(vaccinated)
        CoroutineScope(Dispatchers.Default).launch {
            googleDriveClient.uploadLocalDatabase()
        }
        return result
    }

    fun deleteWithId(id: Long) {
        vaccineDao.delete(id)
        CoroutineScope(Dispatchers.Default).launch {
            googleDriveClient.uploadLocalDatabase()
        }
    }

    fun getAllVaccinationsForRabbit(fkRabbit: Long): List<Vaccinated> {
        return vaccinatedDao.getAllVaccinationsForRabbit(fkRabbit)
    }

    fun getAllVaccinationsForLitter(fkLitter: Long): List<Vaccinated> {
        return vaccinatedDao.getAllVaccinationsForLitter(fkLitter)
    }

    fun getVaccinatedById(vaccinatedId: Long): Vaccinated? {
        return vaccinatedDao.getVaccinatedFromId(vaccinatedId)
    }

    fun deleteVaccinatedWithId(id: Long) {
        vaccinatedDao.delete(id)
        CoroutineScope(Dispatchers.Default).launch {
            googleDriveClient.uploadLocalDatabase()
        }
    }

    fun getAllRabbitsVaccinatedWith(id: Long): List<Long> {
        return vaccinatedDao.getAllRabbitsVaccinatedWith(id)
    }

    fun getAllLittersVaccinatedWith(id: Long): List<Long> {
        return vaccinatedDao.getAllLittersVaccinatedWith(id)
    }
}