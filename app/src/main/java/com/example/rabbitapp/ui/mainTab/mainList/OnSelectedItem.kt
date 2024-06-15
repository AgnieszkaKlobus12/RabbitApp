package com.example.rabbitapp.ui.mainTab.mainList

import com.example.rabbitapp.model.entities.HomeListItem

interface OnSelectedItem {

    fun onItemClick(item: HomeListItem)

}