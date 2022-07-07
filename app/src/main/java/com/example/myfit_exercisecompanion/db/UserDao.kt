package com.example.myfit_exercisecompanion.db

import android.net.Uri
import com.example.myfit_exercisecompanion.models.User


interface UserDao {
    suspend fun getUser(): User?
    suspend fun insertUser(user: User, uri: Uri? = null): Boolean
    suspend fun updateUser(map: Map<String, Any>, uri: Uri? = null): Boolean
    suspend fun deleteUser(email: String): Boolean
}