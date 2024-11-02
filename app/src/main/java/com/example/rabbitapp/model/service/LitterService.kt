package com.example.rabbitapp.model.service

import com.example.rabbitapp.model.entities.Litter
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.runBlocking

class LitterService(private val supabase: SupabaseClient) {

    fun getAll(): List<Litter> {
        var result: List<Litter>
        runBlocking {
            result = supabase.from("litter").select().decodeList<Litter>()
        }
        return result
    }

    fun deleteWithId(it: Long) {
        runBlocking {
            supabase.from("litter").delete {
                filter {
                    Litter::id eq it
                }
            }
        }
    }

    fun save(litter: Litter): Long {
        var result: Long? = null
        runBlocking {
            result = supabase.from("litter").upsert(litter) {
                select()
            }.decodeSingle<Litter>().id
        }
        return result!!
    }

    fun getLitterFromId(id: Long): Litter? {
        var result: Litter? = null
        runBlocking {
            result = supabase.from("litter").select {
                filter {
                    Litter::id eq id
                }
            }.decodeSingle<Litter>()
        }
        return result
    }

    fun markLitterAsDead(litterId: Long, date: Long) {
        runBlocking {
            supabase.from("litter").update(
                {
                    Litter::deathDate setTo date
                }
            ) {
                select()
                filter {
                    Litter::id eq litterId
                }
            }
        }
    }

}