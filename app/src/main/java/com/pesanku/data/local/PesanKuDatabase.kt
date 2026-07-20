package com.pesanku.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pesanku.data.local.entity.ReminderEntity

@Database(
    entities = [ReminderEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PesanKuDatabase : RoomDatabase() {

    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: PesanKuDatabase? = null

        fun create(context: Context): PesanKuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PesanKuDatabase::class.java,
                    "pesanku_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
