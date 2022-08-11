package com.example.myfit_exercisecompanion.ui.fragments.calorieCalculator

import android.app.ProgressDialog.show
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myfit_exercisecompanion.R
import com.example.myfit_exercisecompanion.databinding.FragmentCreateFoodBinding
import com.example.myfit_exercisecompanion.models.FoodItem
import com.example.myfit_exercisecompanion.ui.fragments.home.HomeFragmentDirections
import com.example.myfit_exercisecompanion.ui.viewModels.RunSessionViewModel
import com.example.myfit_exercisecompanion.ui.viewModels.SearchViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateFoodFragment : Fragment(R.layout.fragment_create_food) {
    private var _binding: FragmentCreateFoodBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()
    private val runViewModel: RunSessionViewModel by viewModels()
    private var email = ""

    private val navigationArgs: SearchFoodFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateFoodBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runViewModel.getAuthUser()?.let { email = it.email!! }

        binding.apply {
            btnCreate.setOnClickListener { createFood() }
            btnCancel.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun createFood() {
        // Get user input
        val name = binding.etName.text.toString()
        val calories = binding.etCalories.text.toString()
        val protein = binding.etProtein.text.toString()
        val carbs = binding.etCarbs.text.toString()
        val fat = binding.etFats.text.toString()
        val quantity = binding.etQuantity.text.toString()
        val unit = binding.etUnits.text.toString()

        // Create object
        if (isEntryValid(email, name, calories, protein, carbs, fat)) {
            val newFoodItem = FoodItem(
                email = email,
                food_name = name,
                calories = calories,
                protein = protein,
                carbs = carbs,
                fat = fat,
                serving_qty = quantity,
                serving_unit = unit,
                category = navigationArgs.foodCategory
            )
            // Add to room
            viewModel.addFood(newFoodItem)
            findNavController().navigateUp()
        }
        else{
            Snackbar.make(
                binding.root,
                "A field is missing",
                Snackbar.LENGTH_SHORT
            ).apply {
                setAction("OKAY") { dismiss() }
                show()
            }
        }
    }

    fun isEntryValid(
        email: String,
        name: String,
        calories: String,
        protein: String,
        carbs: String,
        fat: String
    ): Boolean {
        if (email.isBlank() || name.isBlank() || calories.isBlank() || protein.isBlank() || carbs.isBlank() || fat.isBlank()) {
            return false
        }
        return true
    }
}