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

    init {
        val database = AppDatabase.getInstance(application)
        rabbitRepository = RabbitService(database.rabbitRepository())
        litterRepository = LitterService(database.litterRepository())
    }

    fun save(rabbit: Rabbit) {
        rabbitRepository.save(rabbit)
    }

    fun save(litter: Litter) {
        litterRepository.save(litter)
    }

    fun getAll(): List<HomeListItem> {
        val listRabbit = getAllRabbits()
        val listLitter = litterRepository.getAll()
        Log.d("VIEW_MODEL", "Litter list acquired. Size: " + listLitter.size)
        return listRabbit + listLitter
    }

    private fun getAllRabbits(): List<HomeListItem> {
        val listRabbit = rabbitRepository.getAll()
        Log.d("VIEW_MODEL", "Rabbit list acquired. Size: " + listRabbit.size)
        return listRabbit
    }

    fun getRabbitFromId(id: Long): Rabbit? {
        val rabbit = rabbitRepository.getRabbitFromId(id)
        Log.d("VIEW_MODEL", "Rabbit $rabbit acquired.")
        return rabbit
    }

    fun getAllRabbits(gender: Gender): List<HomeListItem> {
        return rabbitRepository.getAllWithGender(gender)
    }

    fun clearParents() {
        selectedRabbit = null
        selectedFather = null
        selectedMother = null
    }
}

