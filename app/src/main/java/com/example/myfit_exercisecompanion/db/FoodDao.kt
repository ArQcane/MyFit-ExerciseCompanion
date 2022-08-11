package com.example.myfit_exercisecompanion.db

import androidx.room.*
import com.example.myfit_exercisecompanion.models.FoodItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(food: FoodItem)

    @Delete
    suspend fun delete(food: FoodItem)

    // update consumed
    @Query("UPDATE foods SET consumed = :isConsumed WHERE id = :id")
    suspend fun update(isConsumed: Boolean, id: Int)

    @Query("DELETE FROM foods where email = :email")
    suspend fun deleteAllItems(email: String)

    @Query("SELECT * from foods where category = :category and email = :email order by id")   // order by add date?
    fun getFoodList(category: String, email: String): Flow<List<FoodItem>>

    @Query("SELECT SUM(calories) from foods WHERE consumed = 1 and email =:email")
    fun getTotalCalories(email: String): Flow<Int>

    @Query("SELECT SUM(protein) from foods WHERE consumed = 1 and email =:email")
    fun getTotalProtein(email: String): Flow<Int>

    @Query("SELECT SUM(carbs) from foods WHERE consumed = 1 and email =:email")
    fun getTotalCarbsConsumed(email: String): Flow<Int>

    @Query("SELECT SUM(fat) from foods WHERE consumed = 1 and email =:email")
    fun getTotalFatConsumed(email: String): Flow<Int>

    @Query("SELECT SUM(protein) from foods WHERE category = :category and email =:email")
    fun getTotalProteinFromCategory(category: String, email: String): Flow<Int>

    @Query("SELECT SUM(carbs) from foods WHERE category = :category and email =:email")
    fun getTotalCarbsFromCategory(category: String, email: String): Flow<Int>

    @Query("SELECT SUM(fat) from foods WHERE category = :category and email =:email")
    fun getTotalFatFromCategory(category: String, email: String): Flow<Int>
}