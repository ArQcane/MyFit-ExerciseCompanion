package com.example.myfit_exercisecompanion.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.example.myfit_exercisecompanion.databinding.ActivityDetailsBinding
import com.example.myfit_exercisecompanion.ui.MainActivity
import com.example.myfit_exercisecompanion.ui.viewModels.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViews()
        setUIListeners()
        setUpViewModelListener()
    }

    private fun setUpViews() {
        authViewModel.username?.let { binding.usernameInput.editText?.setText(it) }
        authViewModel.weight?.let { binding.weightInput.editText?.setText(it) }
        authViewModel.height?.let { binding.heightInput.editText?.setText(it) }
    }

    private fun setUIListeners() {
        binding.usernameInput.editText?.addTextChangedListener { text ->
            binding.usernameInput.isErrorEnabled = false
            authViewModel.username = text?.toString()
        }
        binding.weightInput.editText?.addTextChangedListener { text ->
            binding.weightInput.isErrorEnabled = false
            authViewModel.weight = text?.toString()
        }
        binding.heightInput.editText?.addTextChangedListener { text ->
            binding.heightInput.isErrorEnabled = false
            authViewModel.height = text?.toString()
        }
        binding.btnSubmit.setOnClickListener {
            authViewModel.insertUserIntoFirestore()
        }
    }

    private fun setUpViewModelListener() {
        authViewModel.authState.observe(this) {
            when (it) {
                is AuthViewModel.AuthState.Loading -> binding.apply {
                    overlay.visibility = View.VISIBLE
                    progress.visibility = View.VISIBLE
                }
                is AuthViewModel.AuthState.Success -> binding.apply {
                    hideLoading()
                    Intent(this@DetailsActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(this)
                        finish()
                    }
                }
                is AuthViewModel.AuthState.InvalidUsername -> binding.apply {
                    hideLoading()
                    usernameInput.isErrorEnabled = true
                    usernameInput.error = it.message
                }
                is AuthViewModel.AuthState.InvalidWeight -> binding.apply {
                    hideLoading()
                    weightInput.isErrorEnabled = true
                    weightInput.error = it.message
                }
                is AuthViewModel.AuthState.InvalidHeight -> binding.apply {
                    hideLoading()
                    heightInput.isErrorEnabled = true
                    heightInput.error = it.message
                }
                is AuthViewModel.AuthState.FireBaseFailure -> binding.apply {
                    hideLoading()
                    Snackbar.make(
                        binding.root,
                        it.message ?: "Unknown error has occurred",
                        Snackbar.LENGTH_SHORT
                    ).apply {
                        setAction("OKAY") { dismiss() }
                        show()
                    }
                }
                else -> binding.apply {
                    hideLoading()
                }
            }
        }
    }

    private fun hideLoading() = binding.apply {
        overlay.visibility = View.GONE
        progress.visibility = View.GONE
    }
}