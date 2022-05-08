package com.example.myfit_exercisecompanion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myfit_exercisecompanion.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val userId = intent.getStringExtra("user_id")
        val emailId = intent.getStringExtra("email_id")

        binding.tvUserId.text = "User ID :: $userId"
        binding.tvEmailId.text = "Email ID :: $emailId"

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }
    }
}