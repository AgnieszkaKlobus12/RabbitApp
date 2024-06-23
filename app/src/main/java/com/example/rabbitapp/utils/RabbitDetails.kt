package com.example.rabbitapp.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RabbitDetails {

    companion object {

        private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        fun getAge(birthdate: Long): String {
            val epochDays = System.currentTimeMillis() / 86400000 - birthdate
            val days = epochDays % 30
            val months = epochDays / 30 % 12
            val years = epochDays / 365
            return if (years.toInt() == 0) {
                if ( months.toInt() == 0 ) {
                    "$days d"
                } else {
                    "$months m $days d"
                }
            } else {
                "$years y $months m $days d"
            }
        }

        fun getDateString(birthdate: Long): String? {
            return LocalDate.ofEpochDay(birthdate).format(formatter)
        }

    }
}