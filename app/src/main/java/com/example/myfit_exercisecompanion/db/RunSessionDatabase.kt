package com.example.myfit_exercisecompanion.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myfit_exercisecompanion.models.RunSession

@Database(entities = [RunSession::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RunSessionDatabase : RoomDatabase() {
    abstract fun getRunningSessionDao(): RunSessionDAO

//    companion object {
//        @Volatile
//        private var instance: RunSessionDatabase? = null
//        private val LOCK = Any()
//
//        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
//            instance ?: createDatabase(context).also { instance = it }
//        }
//
//        private fun createDatabase(context: Context) =
//            Room.databaseBuilder(
//                context.applicationContext,
//                RunSessionDatabase::class.java,
//                "myfit_db.db"
//            ).fallbackToDestructiveMigration()
//                .build()
//    }
}