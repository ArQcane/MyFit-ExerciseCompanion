package com.example.myfit_exercisecompanion.repository

import com.example.myfit_exercisecompanion.db.UserDao
import com.example.myfit_exercisecompanion.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    suspend fun getCurrentUser() = withContext(Dispatchers.IO){
        userDao.getUser()
    }
    suspend fun addUser(user: User) = withContext(Dispatchers.IO){
        userDao.insertUser(user)
    }
    suspend fun deleteUser(email: String) = withContext(Dispatchers.IO){
        userDao.deleteUser(email)
    }
    suspend fun updateUser(map: Map<String, Any>) = withContext(Dispatchers.IO){
        userDao.updateUser(map)
    }
}