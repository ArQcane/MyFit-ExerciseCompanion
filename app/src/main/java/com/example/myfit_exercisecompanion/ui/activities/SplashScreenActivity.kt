package com.example.myfit_exercisecompanion.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.myfit_exercisecompanion.R
import com.example.myfit_exercisecompanion.databinding.ActivityDetailsBinding
import com.example.myfit_exercisecompanion.databinding.ActivitySplashScreenBinding
import com.example.myfit_exercisecompanion.ui.MainActivity
import com.example.myfit_exercisecompanion.ui.viewModels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_splash_screen.*

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ivSplashScreenImg.alpha = 0f
        ivSplashScreenImg.animate().setDuration(1500).alpha(1f).withEndAction {
            if(authViewModel.getFirebaseUser()!= null){
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                startActivity(
                    Intent(
                        this,
                        MainActivity::class.java
                    )
                )
            }
            else {
                val i = Intent(this, LoginActivity::class.java)
                startActivity(i)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }
    }
}