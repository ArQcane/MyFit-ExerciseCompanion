package com.example.myfit_exercisecompanion.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myfit_exercisecompanion.R
import com.example.myfit_exercisecompanion.databinding.ActivityMainBinding
import com.example.myfit_exercisecompanion.db.RunSessionDAO
import com.example.myfit_exercisecompanion.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    @Inject
    lateinit var runSessionDao: RunSessionDAO

    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        navigateToTrackingFragmentIfNeeded(intent)


        navController = findNavController(R.id.navHostFragment)

        navigateToTrackingFragmentIfNeeded(intent)

        with(binding) {
            bottomNavigationView.apply {
                setupWithNavController(navController)
                setOnNavigationItemReselectedListener { /* no-op */ }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigationView.visibility = when (destination.id) {
                R.id.homeFragment, R.id.runTrackerFragment, R.id.calorieCounterFragment, R.id.profileFragment -> View.VISIBLE
                else -> View.GONE
            }
        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?){
        if(intent?.action == ACTION_SHOW_TRACKING_FRAGMENT){
            navController.navigate(R.id.action_global_trackingFragment)
        }
    }
}