package com.example.myfit_exercisecompanion.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.calorietracker.adapter.FoodSearchListAdapter
import com.example.myfit_exercisecompanion.R
import com.example.myfit_exercisecompanion.databinding.FragmentSearchFoodBinding
import com.example.myfit_exercisecompanion.models.FoodItem
import com.example.myfit_exercisecompanion.ui.viewModels.AuthViewModel
import com.example.myfit_exercisecompanion.ui.viewModels.RunSessionViewModel
import com.example.myfit_exercisecompanion.ui.viewModels.SearchViewModel
import com.example.myfit_exercisecompanion.utility.convertToFoodItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFoodFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels()
    private val runViewModel: RunSessionViewModel by viewModels()
    private var email = ""


    private var _binding: FragmentSearchFoodBinding? = null
    private val binding get() = _binding!!

    private val navigationArgs: SearchFoodFragmentArgs by navArgs()

    private var _currentCategory: String? = null
    private val currentCategory get() = _currentCategory!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // show menu
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchFoodBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _currentCategory = navigationArgs.foodCategory  // "breakfast"

        // list item click listener
        val adapter = FoodSearchListAdapter { food ->
            val foodItem = convertToFoodItem(food, currentCategory)
            goToFoodDetailFragment(foodItem)
        }

        // data binding
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.rvBananas.adapter = adapter

        // search view click listener
        binding.svFood.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.i("SearchFragment", "$query")
                if (query != null) {
                    binding.tvResult.text = "Searching for: \"$query\""
                    viewModel.getListOf(query)
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                Log.i("SearchFragment", "Typing")
                return false
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_create_food, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_create -> {
                goToCreateFragment()
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun goToCreateFragment() {
        val action =
            SearchFoodFragmentDirections.actionSearchFragmentToCreateFoodFragment(
                foodCategory = currentCategory
            )
        findNavController().navigate(action)

    }

    private fun goToFoodDetailFragment(item: FoodItem) {
        val action =
            SearchFoodFragmentDirections.actionSearchFragmentToFoodDetailFragment(
                foodItem = item)
        findNavController().navigate(action)
    }
}