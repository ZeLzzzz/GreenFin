package com.example.financeaudit.domain.repository

import com.example.financeaudit.domain.model.User

interface UserRepository {
    suspend fun saveUser(user: User): Result<Boolean>
    suspend fun getUserProfile(userId: String): Result<User>
}