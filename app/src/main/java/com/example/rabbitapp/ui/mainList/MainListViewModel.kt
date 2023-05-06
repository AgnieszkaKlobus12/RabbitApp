package com.example.rabbitapp.ui.mainList

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.rabbitapp.model.AppDatabase
import com.example.rabbitapp.model.entities.HomeListItem
import com.example.rabbitapp.model.entities.Litter
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.model.service.LitterService
import com.example.rabbitapp.model.service.RabbitService
import kotlinx.coroutines.DelicateCoroutinesApi

@OptIn(DelicateCoroutinesApi::class)
class MainListViewModel(application: Application) : AndroidViewModel(application) {

    private val rabbitRepository: RabbitService
    private val litterRepository: LitterService

    init {
        val database = AppDatabase.getInstance(application)
        rabbitRepository = RabbitService(database.rabbitRepository())
        litterRepository = LitterService(database.litterRepository())
    }

    fun save(rabbit: Rabbit) {
        rabbitRepository.save(rabbit);
    }

    fun save(litter: Litter) {
        litterRepository.save(litter);
    }

    fun getAll(): List<HomeListItem> {
        val listRabbit = rabbitRepository.getAll()
        val listLitter = litterRepository.getAll()
        Log.d("VIEW_MODEL", "Rabbit list acquired. Size: " + listRabbit.size)
        Log.d("VIEW_MODEL", "Litter list acquired. Size: " + listLitter.size)

        return listRabbit + listLitter
    }
}

