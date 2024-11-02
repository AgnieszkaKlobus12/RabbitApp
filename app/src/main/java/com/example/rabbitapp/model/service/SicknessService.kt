package com.example.rabbitapp.model.service

import com.example.rabbitapp.model.entities.Sickness
import com.example.rabbitapp.model.entities.relations.Sick
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.runBlocking

class SicknessService(private val supabase: SupabaseClient) {

    fun getAllSicknesses(): List<Sickness> {
        var result: List<Sickness>
        runBlocking {
            result = supabase.from("sickness").select().decodeList<Sickness>()
        }
        return result
    }

    fun getSicknessFromId(sicknessId: Long): Sickness? {
        var result: Sickness? = null
        runBlocking {
            result = supabase.from("sickness").select {
                filter {
                    Sickness::id eq sicknessId
                }
            }.decodeSingle<Sickness>()
        }
        return result
    }

    fun save(sickness: Sickness): Long {
        var result: Long? = null
        runBlocking {
            result = supabase.from("sickness").upsert(sickness) {
                select()
            }.decodeSingle<Sickness>().id
        }
        return result!!
    }

    fun deleteWithId(id: Long) {
        runBlocking {
            supabase.from("sickness").delete {
                filter {
                    Sickness::id eq id
                }
            }
        }
    }

    fun save(sickness: Sick): Long {
        var result: Long? = null
        runBlocking {
            result = supabase.from("sick").upsert(sickness) {
                select()
            }.decodeSingle<Sick>().id
        }
        return result!!
    }

    fun getAllSickForRabbit(it: Long): List<Sick> {
        var result: List<Sick>
        runBlocking {
            result = supabase.from("sick").select {
                filter {
                    Sick::fkRabbit eq it
                }
            }.decodeList<Sick>()
        }
        return result
    }

    fun getAllSickForLitter(it: Long): List<Sick> {
        var result: List<Sick>
        runBlocking {
            result = supabase.from("sick").select {
                filter {
                    Sick::fkLitter eq it
                }
            }.decodeList<Sick>()
        }
        return result
    }

    fun getSickFromId(sickId: Long): Sick? {
        var result: Sick? = null
        runBlocking {
            result = supabase.from("sick").select {
                filter {
                    Sick::id eq sickId
                }
            }.decodeSingle<Sick>()
        }
        return result
    }

    fun deleteSickWithId(id: Long) {
        runBlocking {
            supabase.from("sick").delete {
                filter {
                    Sick::id eq id
                }
            }.decodeSingle<Sick>()
        }
    }

    fun getAllRabbitsWithSickness(id: Long): List<Long> {
        var result: List<Long>
        runBlocking {
            result = supabase.from("sick").select {
                filter {
                    Sick::id eq id
                }
            }.decodeList<Sick>().mapNotNull { it.fkRabbit }
        }
        return result
    }

    fun getAllLittersWithSickness(id: Long): List<Long> {
        var result: List<Long>
        runBlocking {
            result = supabase.from("sick").select {
                filter {
                    Sick::id eq id
                }
            }.decodeList<Sick>().mapNotNull { it.fkLitter }
        }
        return result
    }

}