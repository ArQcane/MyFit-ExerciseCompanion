package com.example.myfit_exercisecompanion.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.myfit_exercisecompanion.databinding.ActivityForgetPasswordBinding
import com.example.myfit_exercisecompanion.ui.viewModels.AuthViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgetPasswordBinding
    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setUpListeners()
        setUpViews()

//        binding.btnBackToLogin.setOnClickListener {
//            onBackPressed()
//        }
//
//        binding.sendRequest.setOnClickListener {
//            val email: String = binding.etForgetPasswordEmail.text.toString().trim { it <= ' ' }
//            if(email.isEmpty()){
//                Toast.makeText(
//                    this@ForgetPasswordActivity,
//                    "Please Enter an existing email address",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//            else{
//                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
//                    .addOnCompleteListener { task ->
//                        if(task.isSuccessful){
//                            Toast.makeText(
//                                this@ForgetPasswordActivity,
//                                "Email sent successfully to reset your password",
//                                Toast.LENGTH_SHORT
//                            ).show()
//
//                            finish()
//                        }else{
//                            Toast.makeText(
//                                this@ForgetPasswordActivity,
//                                task.exception!!.message.toString(),
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//            }
//        }
    }

    private fun setUpViews() {
        authViewModel.email?.let {
            binding.etForgetPasswordEmail.setText(it)
        }
    }

    private fun setUpListeners() {
        binding.apply {
            toolbar.setNavigationOnClickListener { finish() }
            linkSignUp.setOnClickListener {
                startActivity(
                    Intent(
                        this@ForgetPasswordActivity,
                        RegisterActivity::class.java
                    )
                )
            }
            tilForgetPasswordEmail.editText?.addTextChangedListener {
                tilForgetPasswordEmail.isErrorEnabled = false
                authViewModel.email = it.toString()
            }
            sendRequest.setOnClickListener {
                authViewModel.sendPasswordResetLinkToEmail()
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
                    createSnackbar()
                }
                is AuthViewModel.AuthState.InvalidEmail -> binding.apply {
                    progress.visibility = View.GONE
                    overlay.visibility = View.GONE
                    tilForgetPasswordEmail.apply {
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

    private fun createSnackbar() {
        Snackbar.make(
            binding.root,
            "Check your email for further instructions on how to reset your password",
            Snackbar.LENGTH_SHORT
        ).apply {
            setAction("OKAY") { dismiss() }
            addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                override fun onShown(transientBottomBar: Snackbar?) {
                    super.onShown(transientBottomBar)
                    Handler(mainLooper).postDelayed(
                        {
                            if (!this@ForgetPasswordActivity.isFinishing ||
                                !this@ForgetPasswordActivity.isDestroyed
                            ) finish()
                        },
                        3000
                    )
                }
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    finish()
                }
            })
        }.show()
    }
}