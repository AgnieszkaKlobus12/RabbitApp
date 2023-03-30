package com.example.rabbitapp.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.rabbitapp.model.entities.Litter
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.model.entities.Vaccine
import kotlinx.coroutines.*

@Database(
    entities = [Rabbit::class, Vaccine::class, Litter::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

//    abstract fun mountainPassProofDAO(): MountainPassProofDAO

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
//                        val mountainPassProofDAO = db.mountainPassProofDAO()
                        // every table needs to be cleared to avoid redundant data
//                        mountainPassProofDAO.deleteAll()
//                        mountainPassOfficialDAO.insert(MountainPassOfficial())
                    }
                }
            }
        }
    }
}