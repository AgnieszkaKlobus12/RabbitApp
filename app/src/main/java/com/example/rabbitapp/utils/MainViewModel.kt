package com.example.rabbitapp.utils

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.rabbitapp.MainApplication
import com.example.rabbitapp.model.AppDatabase
import com.example.rabbitapp.model.entities.HomeListItem
import com.example.rabbitapp.model.entities.Litter
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.model.entities.Sickness
import com.example.rabbitapp.model.entities.Vaccine
import com.example.rabbitapp.model.entities.relations.Mating
import com.example.rabbitapp.model.entities.relations.Sick
import com.example.rabbitapp.model.entities.relations.Vaccinated
import com.example.rabbitapp.model.service.LitterService
import com.example.rabbitapp.model.service.MatingService
import com.example.rabbitapp.model.service.RabbitService
import com.example.rabbitapp.model.service.SicknessService
import com.example.rabbitapp.model.service.VaccineService
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.DelicateCoroutinesApi

@OptIn(DelicateCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val rabbitRepository: RabbitService
    private val litterRepository: LitterService
    private val vaccinesRepository: VaccineService
    private val matingRepository: MatingService
    private val sickRepository: SicknessService

    var selectedMother: Rabbit? = null
    var selectedFather: Rabbit? = null

    private var googleAccount: GoogleSignInAccount? = null
    private var internet = true
    private var lock = true

    private var googleDriveClient: GoogleDriveClient =
        GoogleDriveClient(application as MainApplication, true)
    val dataRefresh: MutableLiveData<Int> = MutableLiveData()

    init {
        val database = AppDatabase.getInstance(application)
        rabbitRepository = RabbitService(database.rabbitRepository(), googleDriveClient)
        litterRepository = LitterService(database.litterRepository(), googleDriveClient)
        vaccinesRepository =
            VaccineService(
                database.vaccineRepository(),
                database.vaccinatedRepository(),
                googleDriveClient
            )
        matingRepository = MatingService(database.matingRepository(), googleDriveClient)
        sickRepository = SicknessService(
            database.sickRepository(),
            database.sicknessRepository(),
            googleDriveClient
        )
    }

    fun getGoogleDriveClient(): GoogleDriveClient {
        return googleDriveClient
    }

    fun setInternet(editable: Boolean) {
        this.internet = editable
    }

    fun save(rabbit: Rabbit): Long {
        val result = rabbitRepository.save(rabbit)
        return result
    }

    fun update(rabbit: Rabbit) {
        rabbitRepository.update(rabbit)
    }

    fun save(litter: Litter): Long {
        return litterRepository.save(litter)
    }

    fun save(vaccine: Vaccine): Long {
        return vaccinesRepository.save(vaccine)
    }

    fun save(vaccinated: Vaccinated): Long {
        val vaccinatedId = vaccinesRepository.save(vaccinated)
        if (vaccinated.fkLitter != null) {
            val litter = getLitterFromId(vaccinated.fkLitter!!)
            if (litter != null) {
                getAllRabbitFromLitter(litter.id).forEach {
                    vaccinesRepository.save(
                        Vaccinated(
                            0L,
                            vaccinated.date,
                            vaccinated.nextDoseDate,
                            vaccinated.dose,
                            vaccinated.doseNumber,
                            it.id,
                            null,
                            vaccinated.fkVaccine
                        )
                    )
                }
            }
        }
        return vaccinatedId;
    }

    fun getAll(): List<HomeListItem> {
        val listRabbit = getAllRabbitsExcept()
        val listLitter = litterRepository.getAll()
        Log.d("ViewModel", "Litter list acquired. Size: " + listLitter.size)
        return listRabbit + listLitter
    }

    private fun getAllRabbitsExcept(): List<HomeListItem> {
        val listRabbit = rabbitRepository.getAll()
        Log.d("ViewModel", "Rabbit list acquired. Size: " + listRabbit.size)
        return listRabbit
    }


    fun getAllRabbitFromLitter(id: Long): List<Rabbit> {
        val listRabbit = rabbitRepository.getAllFromLitter(id)
        Log.d("ViewModel", "Rabbit list acquired. Size: " + listRabbit.size)
        return listRabbit
    }

    fun getRabbitFromId(id: Long): Rabbit? {
        val rabbit = rabbitRepository.getRabbitFromId(id)
        Log.d("ViewModel", "Rabbit $rabbit acquired.")
        return rabbit
    }

    fun getLitterFromId(fkLitter: Long): Litter? {
        val litter = litterRepository.getLitterFromId(fkLitter)
        Log.d("ViewModel", "Litter $litter acquired.")
        return litter
    }

    fun deleteVaccine(id: Long?) {
        id?.let { vaccinesRepository.deleteWithId(it) }
    }

    fun getAllRabbitsExcept(gender: Gender, ids: List<Long>): List<HomeListItem> {
        return rabbitRepository.getAllWithGenderExcept(gender, ids)
    }

    fun clearSelected() {
        selectedFather = null
        selectedMother = null
    }

    fun getAllVaccines(): List<Vaccine> {
        return vaccinesRepository.getAll()
    }

    fun getVaccine(id: Long): Vaccine? {
        return vaccinesRepository.getById(id)
    }

    fun getAllVaccinationsForRabbit(id: Long): List<Vaccinated> {
        return vaccinesRepository.getAllVaccinationsForRabbit(id)
    }

    fun getAllVaccinationsForLitter(id: Long): List<Vaccinated> {
        return vaccinesRepository.getAllVaccinationsForLitter(id)
    }

    fun save(mating: Mating): Long {
        return matingRepository.save(mating)
    }

    fun getAllLitters(): List<HomeListItem> {
        return litterRepository.getAll()
    }

    fun getMating(matingId: Long): Mating? {
        return matingRepository.getMatingFromId(matingId)
    }

    fun deleteMating(id: Long) {
        return matingRepository.deleteWithId(id)
    }

    fun getAllNotArchivedMating(): List<Mating> {
        return matingRepository.getAllNotArchived()
    }

    fun getAllMatingsForRabbit(id: Long): List<Mating> {
        return matingRepository.getAllMatingsForRabbit(id)
    }

    fun getAllSicknesses(): List<Sickness> {
        return sickRepository.getAllSicknesses()
    }

    fun getSickness(sicknessId: Long): Sickness? {
        return sickRepository.getSicknessFromId(sicknessId)
    }

    fun save(sickness: Sickness): Long {
        return sickRepository.save(sickness)
    }

    fun deleteSickness(id: Long) {
        return sickRepository.deleteWithId(id)
    }

    fun save(sick: Sick): Long {
        return sickRepository.save(sick)
    }

    fun getAllSicknessesForRabbit(it: Long): List<Sick> {
        return sickRepository.getAllSickForRabbit(it)
    }

    fun getAllSicknessesForLitter(it: Long): List<Sick> {
        return sickRepository.getAllSickForLitter(it)
    }

    fun getSick(sickId: Long): Sick? {
        return sickRepository.getSickFromId(sickId)
    }

    fun deleteSick(id: Long) {
        sickRepository.deleteSickWithId(id)
    }

    fun getAllWithSickness(id: Long): List<HomeListItem> {
        val list: MutableList<HomeListItem> = mutableListOf()
        list.addAll(
            sickRepository.getAllRabbitsWithSickness(id)
                .mapNotNull { rabbitId -> rabbitRepository.getRabbitFromId(rabbitId) })
        list.addAll(
            sickRepository.getAllLittersWithSickness(id)
                .mapNotNull { rabbitId -> litterRepository.getLitterFromId(rabbitId) })
        return list
    }

    fun getVaccinatedFromId(vaccinatedId: Long): Vaccinated? {
        return vaccinesRepository.getVaccinatedById(vaccinatedId)
    }

    fun deleteVaccinated(id: Long) {
        vaccinesRepository.deleteVaccinatedWithId(id)
    }

    fun getAllVaccinated(id: Long): List<HomeListItem> {
        val list: MutableList<HomeListItem> = mutableListOf()
        list.addAll(
            vaccinesRepository.getAllRabbitsVaccinatedWith(id)
                .mapNotNull { rabbitId -> rabbitRepository.getRabbitFromId(rabbitId) })
        list.addAll(
            vaccinesRepository.getAllLittersVaccinatedWith(id)
                .mapNotNull { rabbitId -> litterRepository.getLitterFromId(rabbitId) })
        return list
    }

    fun markRabbitAsDead(rabbitId: Long, date: Long) {
        rabbitRepository.markRabbitAsDead(rabbitId, date)
    }

    fun markLitterAsDead(litterId: Long, date: Long) {
        litterRepository.markLitterAsDead(litterId, date)
    }

    fun loginWithGoogle(
        account: GoogleSignInAccount?,
        callback: (success: Boolean) -> Unit
    ) {
        account?.let {
            this.googleAccount = account
            callback.invoke(true)
        } ?: run {
            callback.invoke(false)
        }
        Log.d("ViewModel", "Google account: $googleAccount")
    }

    fun deleteRabbit(id: Long) {
        rabbitRepository.deleteRabbitWithId(id)
    }

    fun deleteLitter(id: Long) {
        litterRepository.deleteWithId(id)
    }

}
