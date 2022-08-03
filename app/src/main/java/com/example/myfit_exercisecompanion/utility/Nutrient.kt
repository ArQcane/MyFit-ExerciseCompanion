package com.example.myfit_exercisecompanion.utility

import com.example.myfit_exercisecompanion.models.Food
import com.example.myfit_exercisecompanion.models.FoodItem
import com.example.myfit_exercisecompanion.models.Nutrient

const val CALORIE_ID = 208
const val PROTEIN_ID = 203
const val CARBS_ID = 205
const val FAT_ID = 204

/** Returns nutrient [String] value **/
fun getNutrient(food: Food, query: Int): String {
    for (nutrient: Nutrient in food.full_nutrients) {
        if (nutrient.attr_id == query.toString()) {
            return nutrient.value.toFloat().toInt().toString()    // round down
        }
    }
    return "0"
}

fun convertToFoodItem(food: Food, category: String): FoodItem {
    return FoodItem(
        food_name = food.food_name,
        serving_qty = food.serving_qty,
        serving_unit = food.serving_unit,
        calories = getNutrient(food, CALORIE_ID),
        photo = food.photo.thumb,
        category = category,
        protein = getNutrient(food, PROTEIN_ID),
        carbs = getNutrient(food, CARBS_ID),
        fat = getNutrient(food, FAT_ID)
    )
}