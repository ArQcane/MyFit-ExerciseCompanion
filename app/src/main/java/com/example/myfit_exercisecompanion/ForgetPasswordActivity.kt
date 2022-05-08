package com.example.myfit_exercisecompanion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myfit_exercisecompanion.databinding.ActivityForgetPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgetPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnBackToLogin.setOnClickListener {
            onBackPressed()
        }

        binding.sendRequest.setOnClickListener {
            val email: String = binding.etForgetPasswordEmail.text.toString().trim { it <= ' ' }
            if(email.isEmpty()){
                Toast.makeText(
                    this@ForgetPasswordActivity,
                    "Please Enter an existing email address",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else{
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            Toast.makeText(
                                this@ForgetPasswordActivity,
                                "Email sent successfully to reset your password",
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()
                        }else{
                            Toast.makeText(
                                this@ForgetPasswordActivity,
                                task.exception!!.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }
}