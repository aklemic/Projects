package com.example.bingetracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Title::class, WatchEntry::class],
    version = 1,
    exportSchema = false
)
abstract class BingeDatabase : RoomDatabase() {

    abstract fun titleDao(): TitleDao
    abstract fun watchEntryDao(): WatchEntryDao

    companion object {
        @Volatile
        private var INSTANCE: BingeDatabase? = null

        fun getInstance(context: Context): BingeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BingeDatabase::class.java,
                    "binge_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
