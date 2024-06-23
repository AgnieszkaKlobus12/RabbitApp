package com.example.rabbitapp.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.rabbitapp.model.dao.LitterDao
import com.example.rabbitapp.model.dao.RabbitDao
import com.example.rabbitapp.model.dao.VaccinatedDao
import com.example.rabbitapp.model.dao.VaccineDao
import com.example.rabbitapp.model.entities.Litter
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.model.entities.Vaccine
import com.example.rabbitapp.model.entities.relations.Vaccinated
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Database(
    entities = [Rabbit::class, Vaccine::class, Litter::class, Vaccinated::class],
    version = 15
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun rabbitRepository(): RabbitDao
    abstract fun litterRepository(): LitterDao
    abstract fun vaccineRepository(): VaccineDao
    abstract fun vaccinatedRepository(): VaccinatedDao

    @DelicateCoroutinesApi
    companion object {
        private var INSTANCE: AppDatabase? = null

        @Synchronized
        fun getInstance(ctx: Context): AppDatabase {
            if (INSTANCE == null)
                INSTANCE = Room.databaseBuilder(
                    ctx.applicationContext, AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .allowMainThreadQueries()
                    .build()
            return INSTANCE!!
        }

        private val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                GlobalScope.launch(Dispatchers.IO) {
                    populateDatabase(INSTANCE!!)
                }
            }

            private suspend fun populateDatabase(db: AppDatabase) {
                db.let { db ->
                    withContext(Dispatchers.IO) {
                        val rabbitRepository = db.rabbitRepository()
                        val litterRepository = db.litterRepository()
                        // every table needs to be cleared to avoid redundant data
                        rabbitRepository.deleteAll()
                        litterRepository.deleteAll()

                        //test data
                        rabbitRepository.insert(
                            Rabbit(
                                1,
                                "Misiek",
                                19144,
                                "MALE",
                                "101",
                                null,
                                null,
                                null,
                                null
                            )
                        )
                        rabbitRepository.insert(
                            Rabbit(
                                2,
                                "Zuzia",
                                19844,
                                "FEMALE",
                                "102",
                                null,
                                null,
                                null,
                                null
                            )
                        )
                        litterRepository.insert(Litter(3, "Miot", 19844, 3, null, 1, 2))
                        rabbitRepository.insert(
                            Rabbit(
                                4,
                                "Miotka",
                                19844,
                                "FEMALE",
                                "103",
                                null,
                                1,
                                2,
                                3
                            )
                        )
                    }
                }
            }
        }
    }
}