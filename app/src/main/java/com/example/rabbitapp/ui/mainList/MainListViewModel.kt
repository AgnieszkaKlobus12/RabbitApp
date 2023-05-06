package com.example.rabbitapp.ui.mainList

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.rabbitapp.model.AppDatabase
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.model.service.RabbitService
import kotlinx.coroutines.DelicateCoroutinesApi

@OptIn(DelicateCoroutinesApi::class)
class MainListViewModel(application: Application) : AndroidViewModel(application) {

    private val rabbitRepository: RabbitService

    init {
        val database = AppDatabase.getInstance(application)
        rabbitRepository = RabbitService(database.rabbitRepository())
    }

    fun save(rabbit: Rabbit) {
        rabbitRepository.save(rabbit);
    }

    fun getAll(): List<Rabbit> {
        val list = rabbitRepository.getAll()
        Log.d("VIEW_MODEL", "Rabbit list acquired. Size: " + list.size)
        return list
    }
}

