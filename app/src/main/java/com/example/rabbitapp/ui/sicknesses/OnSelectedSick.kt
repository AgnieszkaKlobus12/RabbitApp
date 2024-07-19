package com.example.rabbitapp.ui.sicknesses

import com.example.rabbitapp.model.entities.relations.Sick

interface OnSelectedSick {

    fun onItemClick(item: Sick)

}