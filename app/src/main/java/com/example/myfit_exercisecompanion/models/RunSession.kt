package com.example.myfit_exercisecompanion.models

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(tableName = "runsession")
data class RunSession(

   var email: String? = null,
   var img: Bitmap? = null,
   var runSessionTitle: String = "",
   var timestamp: Long = 0L,
   var avgSpeedInKMH: Float = 0f,
   var distanceInMeters: Int = 0,
   var timeInMilis: Long = 0L,
   var caloriesBurnt: Int = 0,
   var stepsPerSession: Int = 0,
): Parcelable {
   @PrimaryKey(autoGenerate = true)
   var id: Int = 0
}