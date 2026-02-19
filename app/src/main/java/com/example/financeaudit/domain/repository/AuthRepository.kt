package com.example.financeaudit.domain.repository

import com.example.financeaudit.domain.model.User

interface AuthRepository {
    fun isUserLoggedIn(): Boolean
    suspend fun signInWithGoogle(idToken: String) : Result<User>
    fun getCurrentUser(): User?
    suspend fun logOut()
}

