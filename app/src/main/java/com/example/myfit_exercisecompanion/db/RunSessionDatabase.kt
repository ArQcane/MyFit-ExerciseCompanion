package com.example.myfit_exercisecompanion.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myfit_exercisecompanion.models.Food
import com.example.myfit_exercisecompanion.models.FoodItem
import com.example.myfit_exercisecompanion.models.RunSession

@Database(entities = [RunSession::class, FoodItem::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RunSessionDatabase : RoomDatabase() {
    abstract fun getRunningSessionDao(): RunSessionDAO
    abstract fun getFoodDao(): FoodDao

}