package com.example.rabbitapp.ui.mainTab

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.rabbitapp.model.AppDatabase
import com.example.rabbitapp.model.entities.HomeListItem
import com.example.rabbitapp.model.entities.Litter
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.model.service.LitterService
import com.example.rabbitapp.model.service.RabbitService
import com.example.rabbitapp.utils.Gender
import kotlinx.coroutines.DelicateCoroutinesApi

@OptIn(DelicateCoroutinesApi::class)
class MainListViewModel(application: Application) : AndroidViewModel(application) {

    private val rabbitRepository: RabbitService
    private val litterRepository: LitterService

    var selectedMother: Rabbit? = null
    var selectedFather: Rabbit? = null

    var selectedRabbit: Rabbit? = null
    var selectedLitter: Litter? = null

    init {
        val database = AppDatabase.getInstance(application)
        rabbitRepository = RabbitService(database.rabbitRepository())
        litterRepository = LitterService(database.litterRepository())
    }

    fun save(rabbit: Rabbit): Long {
        return rabbitRepository.save(rabbit)
    }

    fun update(rabbit: Rabbit) {
        rabbitRepository.update(rabbit)
    }

    fun save(litter: Litter): Long {
        return litterRepository.save(litter)
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

    fun deleteCurrentlySelectedRabbit() {
        Log.d("ViewModel", "Rabbit $selectedRabbit deleted.")
        selectedRabbit?.id?.let { rabbitRepository.deleteRabbitWithId(it) }
    }

    fun deleteCurrentlySelectedLitter() {
        Log.d("ViewModel", "Rabbit $selectedRabbit deleted.")
        selectedLitter?.id?.let { litterRepository.deleteWithId(it) }
    }

    fun getAllRabbitsExcept(gender: Gender, ids: List<Long>): List<HomeListItem> {
        return rabbitRepository.getAllWithGenderExcept(gender, ids)
    }

    fun clearSelected() {
        selectedRabbit = null
        selectedFather = null
        selectedMother = null
        selectedLitter = null
    }
}

