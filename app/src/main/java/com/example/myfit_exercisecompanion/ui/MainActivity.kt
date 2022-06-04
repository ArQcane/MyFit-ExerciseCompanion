package com.example.myfit_exercisecompanion.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myfit_exercisecompanion.R
import com.example.myfit_exercisecompanion.RunTrackerConfigs.MapPresenter
import com.example.myfit_exercisecompanion.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    var presenter = MapPresenter(this)
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.findNavController()

        binding.bottomNavigationView.setupWithNavController(navController)

        val appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment,R.id.runTrackerFragment,R.id.calorieCounterFragment,R.id.profileFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)

        presenter.onViewCreated()


//        val userId = intent.getStringExtra("user_id")
//        val emailId = intent.getStringExtra("email_id")
//
//        binding.tvUserId.text = "User ID :: $userId"
//        binding.tvEmailId.text = "Email ID :: $emailId"
//
//        binding.btnLogout.setOnClickListener {
//            FirebaseAuth.getInstance().signOut()
//
//            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
//            finish()
//        }
    }
}