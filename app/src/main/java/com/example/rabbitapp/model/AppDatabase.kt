package com.example.rabbitapp.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.rabbitapp.model.dao.LitterDao
import com.example.rabbitapp.model.dao.MatingDao
import com.example.rabbitapp.model.dao.RabbitDao
import com.example.rabbitapp.model.dao.SickDao
import com.example.rabbitapp.model.dao.SicknessDao
import com.example.rabbitapp.model.dao.VaccinatedDao
import com.example.rabbitapp.model.dao.VaccineDao
import com.example.rabbitapp.model.entities.Converters
import com.example.rabbitapp.model.entities.Litter
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.model.entities.Sickness
import com.example.rabbitapp.model.entities.Vaccine
import com.example.rabbitapp.model.entities.relations.Mating
import com.example.rabbitapp.model.entities.relations.Sick
import com.example.rabbitapp.model.entities.relations.Vaccinated
import kotlinx.coroutines.DelicateCoroutinesApi

@Database(
    entities = [Rabbit::class, Vaccine::class, Litter::class, Vaccinated::class, Mating::class, Sickness::class, Sick::class],
    version = 31
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun rabbitRepository(): RabbitDao
    abstract fun litterRepository(): LitterDao
    abstract fun vaccineRepository(): VaccineDao
    abstract fun vaccinatedRepository(): VaccinatedDao
    abstract fun matingRepository(): MatingDao
    abstract fun sicknessRepository(): SicknessDao
    abstract fun sickRepository(): SickDao

    @DelicateCoroutinesApi
    companion object {
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_25_26 = object : Migration(25, 26) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE Rabbit ADD COLUMN cageNumber INTEGER")
                db.execSQL("ALTER TABLE Litter ADD COLUMN cageNumber INTEGER")
                db.execSQL("ALTER TABLE Litter ADD COLUMN deathDate INTEGER")
            }
        }

        @Synchronized
        fun getInstance(ctx: Context): AppDatabase {
            if (INSTANCE == null)
                INSTANCE = Room.databaseBuilder(
                    ctx.applicationContext, AppDatabase::class.java,
                    "app_database"
                )
//                    .addMigrations(MIGRATION_24_25, MIGRATION_25_26)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            return INSTANCE!!
        }

    }
}