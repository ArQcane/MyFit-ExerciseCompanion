package com.example.myfit_exercisecompanion.ui.fragments.calorieCalculator

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.myfit_exercisecompanion.R
import com.example.myfit_exercisecompanion.databinding.FragmentFoodSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_food_settings.*

@AndroidEntryPoint
class FoodSettingsFragment : Fragment() {
    private var _binding: FragmentFoodSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFoodSettingsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // Get key, values from shared preferences
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val defaultCalorieGoal = resources.getInteger(R.integer.saved_calorie_goal_default_key)
        val calorieGoal = sharedPref.getInt(getString(R.string.saved_calorie_goal_key), defaultCalorieGoal)
        val defaultProteinGoal = resources.getInteger(R.integer.saved_protein_goal_default_key)
        val proteinGoal = sharedPref.getInt(getString(R.string.saved_protein_goal_key), defaultProteinGoal)
        val defaultCarbsGoal = resources.getInteger(R.integer.saved_carbs_goal_default_key)
        val carbsGoal = sharedPref.getInt(getString(R.string.saved_carbs_goal_key), defaultCarbsGoal)
        val defaultFatGoal = resources.getInteger(R.integer.saved_fat_goal_default_key)
        val fatGoal = sharedPref.getInt(getString(R.string.saved_fat_goal_key), defaultFatGoal)

        // Prepopulate settings with shared preferences
        binding.apply {
            etCalories.setText(calorieGoal.toString())
            etProtein.setText(proteinGoal.toString())
            etCarbs.setText(carbsGoal.toString())
            etFats.setText(fatGoal.toString())
            btnSave.setOnClickListener { saveSettings(sharedPref) }
            btnCancel.setOnClickListener {
                val action = FoodSettingsFragmentDirections.actionFoodSettingsFragmentToCalorieCounterFragment()
                findNavController().navigate(action)
            }
        }

    }

    private fun saveSettings(sharedPref: SharedPreferences) {
        // Get user setting entry
        val calorieGoal = etCalories.text.toString()
        val proteinGoal = etProtein.text.toString()
        val carbsGoal = etCarbs.text.toString()
        val fatGoal = etFats.text.toString()

        // Write to shared preferences
        with(sharedPref.edit()) {
            putInt(getString(R.string.saved_calorie_goal_key), calorieGoal.toInt())
            putInt(getString(R.string.saved_protein_goal_key), proteinGoal.toInt())
            putInt(getString(R.string.saved_carbs_goal_key), carbsGoal.toInt())
            putInt(getString(R.string.saved_fat_goal_key), fatGoal.toInt())
            apply()
        }
        val action = FoodSettingsFragmentDirections.actionFoodSettingsFragmentToCalorieCounterFragment()
        findNavController().navigate(action)
    }
}