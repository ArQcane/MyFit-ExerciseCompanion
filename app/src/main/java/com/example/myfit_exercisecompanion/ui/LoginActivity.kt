package com.example.myfit_exercisecompanion.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.example.myfit_exercisecompanion.databinding.ActivityLoginBinding
import com.example.myfit_exercisecompanion.ui.viewModels.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setUpViews()
        setUpListeners()
    }

    override fun onStart() {
        super.onStart()
        val user = authViewModel.getCurrentUser()
        if (user != null) {
            startActivity(
                Intent(
                    this@LoginActivity,
                    MainActivity::class.java
                )
            )
        } else {
            Timber.i("Error")
        }
    }





    private fun setUpViews() {
        authViewModel.email?.let {
            binding.tilLoginEmail.editText?.setText(it)
        }
        authViewModel.password?.let {
            binding.tilLoginPassword.editText?.setText(it)
        }
    }

    private fun setUpListeners() {
        binding.apply {
            tilLoginEmail.editText?.addTextChangedListener {
                tilLoginEmail.isErrorEnabled = false
                authViewModel.email = it.toString()
            }
            tilLoginPassword.editText?.addTextChangedListener {
                tilLoginPassword.isErrorEnabled = false
                authViewModel.password = it.toString()
            }
            btnLogin.setOnClickListener { authViewModel.logIn() }
            tvRegisterNewAccount2.setOnClickListener {
                startActivity(
                    Intent(
                        this@LoginActivity,
                        RegisterActivity::class.java
                    )
                )
            }
            tvForgetPassword.setOnClickListener {
                startActivity(
                    Intent(
                        this@LoginActivity,
                        ForgetPasswordActivity::class.java
                    )
                )
            }
        }
        authViewModel.authState.observe(this) {
            when (it) {
                is AuthViewModel.AuthState.Loading -> binding.apply {
                    progress.visibility = View.VISIBLE
                    overlay.visibility = View.VISIBLE
                }
                is AuthViewModel.AuthState.Success -> binding.apply {
                    progress.visibility = View.GONE
                    overlay.visibility = View.GONE
                    Intent(
                        this@LoginActivity,
                        MainActivity::class.java
                    ).run {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(this)
                        finish()
                    }
                }
                is AuthViewModel.AuthState.FireBaseFailure -> binding.apply {
                    progress.visibility = View.GONE
                    overlay.visibility = View.GONE
                    Snackbar.make(
                        binding.root,
                        it.message ?: "Unknown error has occurred",
                        Snackbar.LENGTH_SHORT
                    ).apply {
                        setAction("OKAY") { dismiss() }
                        show()
                    }
                }
                is AuthViewModel.AuthState.InvalidEmail -> binding.apply {
                    progress.visibility = View.GONE
                    overlay.visibility = View.GONE
                    tilLoginEmail.apply {
                        isErrorEnabled = true
                        error = it.message
                    }
                }
                is AuthViewModel.AuthState.InvalidPassword -> binding.apply {
                    progress.visibility = View.GONE
                    overlay.visibility = View.GONE
                    tilLoginPassword.apply {
                        isErrorEnabled = true
                        error = it.message
                    }
                }
                else -> binding.apply {
                    progress.visibility = View.GONE
                    overlay.visibility = View.GONE
                }
            }
        }
    }
}