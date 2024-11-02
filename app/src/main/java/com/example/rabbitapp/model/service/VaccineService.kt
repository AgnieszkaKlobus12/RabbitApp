package com.example.rabbitapp.model.service

import com.example.rabbitapp.model.entities.Vaccine
import com.example.rabbitapp.model.entities.relations.Sick
import com.example.rabbitapp.model.entities.relations.Vaccinated
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.runBlocking

class VaccineService(private val supabase: SupabaseClient) {

    fun getAll(): List<Vaccine> {
        var result: List<Vaccine>
        runBlocking {
            result = supabase.from("vaccine").select().decodeList<Vaccine>()
        }
        return result
    }

    fun getById(id: Long): Vaccine? {
        var result: Vaccine? = null
        runBlocking {
            result = supabase.from("vaccine").select {
                filter {
                    Vaccine::id eq id
                }
            }.decodeSingle<Vaccine>()
        }
        return result
    }

    fun save(vaccine: Vaccine): Long {
        var result: Long? = null
        runBlocking {
            result = supabase.from("vaccine").upsert(vaccine) {
                select()
            }.decodeSingle<Vaccine>().id
        }
        return result!!
    }

    fun save(vaccinated: Vaccinated): Long {
        var result: Long? = null
        runBlocking {
            result = supabase.from("vaccinated").upsert(vaccinated) {
                select()
            }.decodeSingle<Vaccinated>().id
        }
        return result!!
    }

    fun deleteWithId(id: Long) {
        runBlocking {
            supabase.from("vaccine").delete {
                filter {
                    Vaccine::id eq id
                }
            }
        }
    }

    fun getAllVaccinationsForRabbit(fkRabbit: Long): List<Vaccinated> {
        var result: List<Vaccinated>
        runBlocking {
            result = supabase.from("vaccinated").select {
                filter {
                    Vaccinated::fkRabbit eq fkRabbit
                }
            }.decodeList<Vaccinated>()
        }
        return result
    }

    fun getAllVaccinationsForLitter(fkLitter: Long): List<Vaccinated> {
        var result: List<Vaccinated>
        runBlocking {
            result = supabase.from("vaccinated").select {
                filter {
                    Vaccinated::fkLitter eq fkLitter
                }
            }.decodeList<Vaccinated>()
        }
        return result
    }

    fun getVaccinatedById(vaccinatedId: Long): Vaccinated? {
        var result: Vaccinated? = null
        runBlocking {
            result = supabase.from("vaccinated").select {
                filter {
                    Vaccinated::id eq vaccinatedId
                }
            }.decodeSingle<Vaccinated>()
        }
        return result
    }

    fun deleteVaccinatedWithId(id: Long) {
        runBlocking {
            supabase.from("vaccinated").delete {
                filter {
                    Vaccinated::id eq id
                }
            }
        }
    }

    fun getAllRabbitsVaccinatedWith(id: Long): List<Long> {
        var result: List<Long>
        runBlocking {
            result = supabase.from("vaccinated").select {
                filter {
                    Vaccinated::id eq id
                }
            }.decodeList<Sick>().mapNotNull { it.fkRabbit }
        }
        return result
    }

    fun getAllLittersVaccinatedWith(id: Long): List<Long> {
        var result: List<Long>
        runBlocking {
            result = supabase.from("vaccinated").select {
                filter {
                    Vaccinated::id eq id
                }
            }.decodeList<Sick>().mapNotNull { it.fkLitter }
        }
        return result
    }
}