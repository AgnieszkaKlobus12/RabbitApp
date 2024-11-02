package com.example.rabbitapp.model.service

import android.util.Log
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.utils.Gender
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.runBlocking

class RabbitService(private val supabase: SupabaseClient) {

    fun getAll(): List<Rabbit> {
        var result: List<Rabbit>
        runBlocking {
            result = supabase.from("rabbit").select().decodeList<Rabbit>()
        }
        return result
    }

    fun getRabbitFromId(id: Long): Rabbit? {
        var result: Rabbit? = null
        runBlocking {
            result = supabase.from("rabbit").select {
                filter {
                    Rabbit::id eq id
                }
            }.decodeSingle<Rabbit>()
        }
        return result
    }

    fun deleteRabbitWithId(id: Long) {
        runBlocking {
            supabase.from("rabbit").delete {
                filter {
                    Rabbit::id eq id
                }
            }
        }
    }

    fun getAllWithGenderExcept(gender: Gender, id: List<Long>): List<Rabbit> {
        var result: List<Rabbit>
        runBlocking {
            result = supabase.from("rabbit").select {
                filter {
                    and {
                        Rabbit::id neq id
                        Rabbit::sex eq gender.name
                    }
                }
            }.decodeList<Rabbit>()
        }
        return result
    }

    fun save(rabbit: Rabbit): Long {
        Log.d("DATABASE", "Rabbit $rabbit saved")
        var result: Long? = null
        runBlocking {
            result = supabase.from("rabbit").upsert(rabbit) {
                select()
            }.decodeSingle<Rabbit>().id
        }
        return result!!
    }

    fun update(rabbit: Rabbit) {
        Log.d("DATABASE", "Rabbit $rabbit updated")
        runBlocking {
            supabase.from("litter").upsert(rabbit)
        }
    }

    fun getAllFromLitter(id: Long): List<Rabbit> {
        var result: List<Rabbit>
        runBlocking {
            result = supabase.from("rabbit").select {
                filter {
                    Rabbit::fkLitter eq id
                }
            }.decodeList<Rabbit>()
        }
        return result
    }

    fun markRabbitAsDead(rabbitId: Long, date: Long) {
        runBlocking {
            supabase.from("rabbit").update(
                {
                    Rabbit::deathDate setTo date
                }
            ) {
                select()
                filter {
                    Rabbit::id eq rabbitId
                }
            }
        }
    }

}