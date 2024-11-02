package com.example.rabbitapp.model.service

import com.example.rabbitapp.model.entities.relations.Mating
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.runBlocking

class MatingService(private val supabase: SupabaseClient) {

    fun getAll(): List<Mating> {
        var result: List<Mating>
        runBlocking {
            result = supabase.from("mating").select().decodeList<Mating>()
        }
        return result
    }

    fun deleteWithId(it: Long) {
        runBlocking {
            supabase.from("mating").delete {
                filter {
                    Mating::id eq it
                }
            }
        }
    }

    fun save(mating: Mating): Long {
        var result: Long? = null
        runBlocking {
            result = supabase.from("mating").upsert(mating) {
                select()
            }.decodeSingle<Mating>().id
        }
        return result!!
    }

    fun getMatingFromId(id: Long): Mating? {
        var result: Mating? = null
        runBlocking {
            result = supabase.from("mating").select {
                filter {
                    Mating::id eq id
                }
            }.decodeSingle<Mating>()
        }
        return result
    }

    fun getAllNotArchived(): List<Mating> {
        var result: List<Mating>
        runBlocking {
            result = supabase.from("mating").select {
                filter {
                    Mating::archived eq false
                }
            }.decodeList<Mating>()
        }
        return result
    }

    fun getAllMatingsForRabbit(id: Long): List<Mating> {
        var result: List<Mating>
        runBlocking {
            result = supabase.from("mating").select {
                filter {
                    or {
                        Mating::fkFather eq id
                        Mating::fkMother eq id
                    }
                }
            }.decodeList<Mating>()
        }
        return result
    }
}