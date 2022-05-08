package com.example.myfit_exercisecompanion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.myfit_exercisecompanion.databinding.ActivityRegisterBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.tvAlreadyHaveAnAccount.setOnClickListener {
            onBackPressed()
        }

        binding.btnRegister.setOnClickListener {
            when{
                TextUtils.isEmpty(binding.etRegisterEmail.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please enter your email",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(binding.etRegisterPassword.text.toString().trim{ it <= ' '}) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please enter your password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {

                    val email : String = binding.etRegisterEmail.text.toString().trim { it <= ' ' }
                    val password: String = binding.etRegisterPassword.text.toString().trim { it <= ' ' }

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(
                            OnCompleteListener<AuthResult> { task ->
                                if(task.isSuccessful){

                                    val firebaseUser : FirebaseUser = task.result!!.user!!

                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "You are registered successfully.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    intent.putExtra("user_id", firebaseUser.uid)
                                    intent.putExtra("email_id", email)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        task.exception!!.message.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                }
            }
        }
    }
}