package com.example.myfit_exercisecompanion.ui.viewModels

import android.util.Log
import androidx.lifecycle.*
import com.example.myfit_exercisecompanion.db.FoodDao
import com.example.myfit_exercisecompanion.models.Food
import com.example.myfit_exercisecompanion.models.FoodItem
import com.example.myfit_exercisecompanion.network.FoodApi
import com.example.myfit_exercisecompanion.network.FoodApiService
import com.example.myfit_exercisecompanion.ui.viewModels.FoodApiStatus.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class FoodApiStatus { LOADING, ERROR, DONE }

@HiltViewModel
class SearchViewModel @Inject constructor(private val foodDao: FoodDao) : ViewModel() {

    private val _status = MutableLiveData<FoodApiStatus>()
    val status: LiveData<FoodApiStatus> = _status

    private val _currentFood = MutableLiveData<String>()
    val currFood: LiveData<String> = _currentFood

    private val _foods = MutableLiveData<List<Food>>()
    val foods: LiveData<List<Food>> = _foods

    init {
        getListOf("banana") // default search init
    }

    fun addFood(food: FoodItem) {
        viewModelScope.launch {
            foodDao.insert(food)
        }
    }

    fun deleteFood(food: FoodItem) {
        viewModelScope.launch {
            foodDao.delete(food)
        }
    }

    fun getListOf(food: String) {
        _currentFood.value = food
        _status.value = LOADING
        try {
            viewModelScope.launch {
                Log.i("SearchViewModel", "Launching query")
                _foods.value = FoodApi.retrofitService.getListOf(food, true).common
                _status.value = DONE
                Log.i("SearchViewModel", _foods.value.toString())

            }
        } catch (e: Exception) {
            _foods.value = listOf()
            _status.value = ERROR
        }
    }
}