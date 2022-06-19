package com.example.myfit_exercisecompanion.di

import com.example.myfit_exercisecompanion.db.UserDao
import com.example.myfit_exercisecompanion.db.UserDaoImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object FirestoreModule {
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth =
        FirebaseAuth.getInstance()

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore =
        FirebaseFirestore.getInstance()

    @Provides
    fun provideUserDao(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore
    ): UserDao = UserDaoImpl(firebaseAuth, firebaseFirestore)
}