package com.example.rabbitapp.ui.mainList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rabbitapp.model.AppDatabase
import com.example.rabbitapp.model.entities.HomeListItem
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

    private val _mainRabbitsItems = MutableLiveData<List<HomeListItem>>().apply {
        value = rabbitRepository.getAll().value
//        value = listOf(Rabbit(0, "aaaa", 47862582478, "Male"))
    }
    val mainRabbitsItems: LiveData<List<HomeListItem>> = _mainRabbitsItems

}