package com.example.myfit_exercisecompanion.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.installations.time.SystemClock
import java.io.Serializable

@Entity(tableName = "runsession")
data class RunSession(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "user")
    var user: String?,
    @ColumnInfo(name = "content")
    var content: String?,
    @ColumnInfo(name = "title")
    var title: String?,
    @ColumnInfo(name = "steps")
    var steps: Int?,
    @ColumnInfo(name = "distance")
    var distance: Double?,
    @ColumnInfo(name = "time")
    var time: String?,
    @ColumnInfo(name = "pace")
    var pace: Double?,
    @ColumnInfo(name = "calories_burnt")
    var caloriesBurnt: Int?
) : Serializable