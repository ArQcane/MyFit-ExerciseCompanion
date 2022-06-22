package com.example.myfit_exercisecompanion.db

import com.example.myfit_exercisecompanion.models.User


interface UserDao {
    suspend fun getUser(): User?
    suspend fun insertUser(user: User): Boolean
    suspend fun updateUser(map: Map<String, Any>): Boolean
    suspend fun deleteUser(email: String): Boolean
}