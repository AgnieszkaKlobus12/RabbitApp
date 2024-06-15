package com.example.rabbitapp.ui.vaccines

import com.example.rabbitapp.model.entities.Vaccine

interface OnSelectedVaccine {

    fun onItemClick(item: Vaccine)

}