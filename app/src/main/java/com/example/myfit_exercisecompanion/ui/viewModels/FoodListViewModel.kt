package com.example.myfit_exercisecompanion.ui.viewModels

import androidx.lifecycle.*
import com.example.myfit_exercisecompanion.db.FoodDao
import com.example.myfit_exercisecompanion.models.FoodItem
import com.example.myfit_exercisecompanion.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodListViewModel @Inject constructor(private val foodDao: FoodDao, private val authRepository: AuthRepository) : ViewModel() {

    private val _breakfastItems = foodDao.getFoodList("breakfast", authRepository.getAuthUser()?.email!!)
    val breakfastItems: LiveData<List<FoodItem>> = _breakfastItems.asLiveData()

    private val _lunchItems = foodDao.getFoodList("lunch", authRepository.getAuthUser()?.email!!)
    val lunchItems: LiveData<List<FoodItem>> = _lunchItems.asLiveData()

    private val _totalCalories = foodDao.getTotalCalories(authRepository.getAuthUser()?.email!!)
    val totalCalories: LiveData<Int> = _totalCalories.asLiveData()

    private val _totalProtein = foodDao.getTotalProtein(authRepository.getAuthUser()?.email!!)
    val totalProtein: LiveData<Int> = _totalProtein.asLiveData()

    private val _totalCarbsConsumed = foodDao.getTotalCarbsConsumed(authRepository.getAuthUser()?.email!!)
    val totalCarbsConsumed: LiveData<Int> = _totalCarbsConsumed.asLiveData()

    private val _totalFatsConsumed = foodDao.getTotalFatConsumed(authRepository.getAuthUser()?.email!!)
    val totalFatsConsumed: LiveData<Int> = _totalFatsConsumed.asLiveData()

    private val _totalBreakfastProtein = foodDao.getTotalProteinFromCategory("breakfast", authRepository.getAuthUser()?.email!!)
    val totalBreakfastProtein: LiveData<Int> = _totalBreakfastProtein.asLiveData()

    private val _totalBreakfastCarbs = foodDao.getTotalCarbsFromCategory("breakfast", authRepository.getAuthUser()?.email!!)
    val totalBreakfastCarbs: LiveData<Int> = _totalBreakfastCarbs.asLiveData()

    private val _totalBreakfastFat = foodDao.getTotalFatFromCategory("breakfast", authRepository.getAuthUser()?.email!!)
    val totalBreakfastFat: LiveData<Int> = _totalBreakfastFat.asLiveData()

    fun updateConsumed(isConsumed: Boolean, id: Int) {
        viewModelScope.launch {
            foodDao.update(isConsumed, id)
        }
    }

    fun deleteFood(food_item: FoodItem) {
        viewModelScope.launch {
            foodDao.delete(food_item)
        }
    }
}