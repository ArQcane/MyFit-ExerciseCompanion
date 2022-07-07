package com.example.myfit_exercisecompanion.repository

import android.net.Uri
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
    suspend fun addUser(user: User, uri: Uri? = null) = withContext(Dispatchers.IO){
        userDao.insertUser(user,uri)
    }
    suspend fun deleteUser(email: String) = withContext(Dispatchers.IO){
        userDao.deleteUser(email)
    }
    suspend fun updateUser(map: Map<String, Any>, uri: Uri? = null) = withContext(Dispatchers.IO){
        userDao.updateUser(map, uri)
    }
}