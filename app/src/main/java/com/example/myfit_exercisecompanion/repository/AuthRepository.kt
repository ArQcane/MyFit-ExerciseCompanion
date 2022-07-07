package com.example.myfit_exercisecompanion.repository

import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sign


@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    fun getAuthUser() = firebaseAuth.currentUser

    fun login(email: String, password: String) =
        firebaseAuth.signInWithEmailAndPassword(email, password)

    fun signUp(email: String, password: String) =
        firebaseAuth.createUserWithEmailAndPassword(email, password)

    fun sendPasswordResetLink(email: String) =
        firebaseAuth.sendPasswordResetEmail(email)

    fun signOut() = firebaseAuth.signOut()
}