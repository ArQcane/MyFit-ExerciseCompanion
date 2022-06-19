package com.example.myfit_exercisecompanion.models

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "runsession")
data class RunSession(
   var img: Bitmap? = null,
   var runSessionTitle: String = "",
   var timestamp: Long = 0L,
   var avgSpeedInKMH: Float = 0f,
   var distanceInMeters: Int = 0,
   var timeInMilis: Long = 0L,
   var caloriesBurnt: Int = 0,
   var stepsPerSession: Int = 0
){
   @PrimaryKey(autoGenerate = true)
   var id: Int? = null
}