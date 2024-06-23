package com.example.rabbitapp.ui.vaccines

import com.example.rabbitapp.model.entities.relations.Vaccinated

interface OnSelectedVaccination {

    fun onItemClick(item: Vaccinated)

}