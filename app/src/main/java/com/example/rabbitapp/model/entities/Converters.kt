package com.example.rabbitapp.model.entities

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


object Converters {

    @TypeConverter
    fun fromString(value: String?): List<String> {
        val listType: Type = object : TypeToken<List<String?>?>() {}.type
        return Gson().fromJson(value, listType) ?: mutableListOf()
    }

    @TypeConverter
    fun fromList(list: List<String>?): String {
        val gson = Gson()
        val json = gson.toJson(list)
        return json
    }
}