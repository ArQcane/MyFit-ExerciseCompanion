package com.example.myfit_exercisecompanion.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

data class FoodResponse(
    val common: List<Food>
)

/** Food object from API **/
data class Food(
    val tag_id: String,
    val food_name: String,
    val serving_qty: String,
    val serving_unit: String,
    val full_nutrients: List<Nutrient>,
    val photo: Photo,
)

data class Nutrient(
    val value: String,
    val attr_id: String
)

data class Photo(
    val thumb: String
)

/** Save Food object into room **/
@Parcelize
@Entity(tableName = "foods")
data class FoodItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,   // init to 0 so we dont have to pass id parameter
    @ColumnInfo(name = "email") val email: String = "",
    @ColumnInfo(name = "name") val food_name: String = "",
    @ColumnInfo(name = "qty") val serving_qty: String = "",
    @ColumnInfo(name = "unit") val serving_unit: String = "",
    @ColumnInfo(name = "calories") val calories: String = "",
    @ColumnInfo(name = "photos") val photo: String = "",
    @ColumnInfo(name = "category") val category: String = "",
    @ColumnInfo(name = "consumed") val consumed: Boolean = true,    // init to true
    @ColumnInfo(name = "protein") val protein: String = "",
    @ColumnInfo(name = "carbs") val carbs: String = "",
    @ColumnInfo(name = "fat") val fat: String = ""
) : Parcelable
