package com.example.myfit_exercisecompanion.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.example.myfit_exercisecompanion.databinding.ActivityRegisterBinding
import com.example.myfit_exercisecompanion.ui.viewModels.AuthViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setUpListeners()
        setUpViews()

//        binding.tvAlreadyHaveAnAccount.setOnClickListener {
//            onBackPressed()
//        }
//
//        binding.btnRegister.setOnClickListener {
//            when{
//                TextUtils.isEmpty(binding.etRegisterEmail.text.toString().trim { it <= ' ' }) -> {
//                    Toast.makeText(
//                        this@RegisterActivity,
//                        "Please enter your email",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//
//                TextUtils.isEmpty(binding.etRegisterPassword.text.toString().trim{ it <= ' '}) -> {
//                    Toast.makeText(
//                        this@RegisterActivity,
//                        "Please enter your password",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//                else -> {
//
//                    val email : String = binding.etRegisterEmail.text.toString().trim { it <= ' ' }
//                    val password: String = binding.etRegisterPassword.text.toString().trim { it <= ' ' }
//
//                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
//                        .addOnCompleteListener(
//                            OnCompleteListener<AuthResult> { task ->
//                                if(task.isSuccessful){
//
//                                    val firebaseUser : FirebaseUser = task.result!!.user!!
//
//                                    Toast.makeText(
//                                        this@RegisterActivity,
//                                        "You are registered successfully.",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//
//                                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
//                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                    intent.putExtra("user_id", firebaseUser.uid)
//                                    intent.putExtra("email_id", email)
//                                    startActivity(intent)
//                                    finish()
//                                } else {
//                                    Toast.makeText(
//                                        this@RegisterActivity,
//                                        task.exception!!.message.toString(),
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                }
//                            }
//                        )
//                }
//            }
//        }
    }
    private fun setUpViews() {
        authViewModel.email?.let {
            binding.tilRegisterEmail.editText?.setText(it)
        }
        authViewModel.password?.let {
            binding.tilRegisterPassword.editText?.setText(it)
        }
        authViewModel.confirmPassword?.let {
            binding.tilConfirmPassword.editText?.setText(it)
        }
    }

    private fun setUpListeners() {
        binding.apply {
            toolbar.setNavigationOnClickListener { finish() }
            tvAlreadyHaveAnAccount.setOnClickListener {
                startActivity(
                    Intent(
                        this@RegisterActivity,
                        LoginActivity::class.java
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
            }
            tilRegisterEmail.editText?.addTextChangedListener {
                tilRegisterEmail.isErrorEnabled = false
                authViewModel.email = it.toString()
            }
            tilRegisterPassword.editText?.addTextChangedListener {
                tilRegisterPassword.isErrorEnabled = false
                tilConfirmPassword.isErrorEnabled = false
                authViewModel.password = it.toString()
            }
            tilConfirmPassword.editText?.addTextChangedListener {
                tilConfirmPassword.isErrorEnabled = false
                tilRegisterPassword.isErrorEnabled = false
                authViewModel.confirmPassword = it.toString()
            }
            btnRegister.setOnClickListener { authViewModel.signUp() }
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
                    tilRegisterEmail.apply {
                        isErrorEnabled = true
                        error = it.message
                    }
                }
                is AuthViewModel.AuthState.InvalidPassword -> binding.apply {
                    progress.visibility = View.GONE
                    overlay.visibility = View.GONE
                    tilRegisterPassword.apply {
                        isErrorEnabled = true
                        error = it.message
                    }
                }
                is AuthViewModel.AuthState.InvalidConfirmPassword -> binding.apply {
                    progress.visibility = View.GONE
                    overlay.visibility = View.GONE
                    tilConfirmPassword.apply {
                        isErrorEnabled = true
                        error = it.message
                    }
                    tilRegisterPassword.apply {
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
            "Successfully created account!",
            Snackbar.LENGTH_SHORT
        ).apply {
            setAction("OKAY") { dismiss() }
            addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                override fun onShown(transientBottomBar: Snackbar?) {
                    super.onShown(transientBottomBar)
                    Handler(mainLooper).postDelayed(
                        {
                            if (
                                !this@RegisterActivity.isFinishing ||
                                !this@RegisterActivity.isDestroyed
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