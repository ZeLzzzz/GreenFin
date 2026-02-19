package com.example.financeaudit.data.repository

import android.content.Context
import com.example.financeaudit.domain.model.User
import com.example.financeaudit.domain.repository.AuthRepository
import com.example.financeaudit.presentation.util.getGoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val context: Context
) : AuthRepository {
    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                val user = User(
                    uid = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "No Name",
                    email = firebaseUser.email ?: "",
                    photoUrl = firebaseUser.photoUrl?.toString() ?: "",
                    lastLogin = System.currentTimeMillis(),
                )
                Result.success(user)
            } else {
                Result.failure(Exception("Sign in successful but user data is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser
        return if (firebaseUser != null) {
            User(
                uid = firebaseUser.uid,
                name = firebaseUser.displayName ?: "No Name",
                email = firebaseUser.email ?: "",
                photoUrl = firebaseUser.photoUrl?.toString() ?: "",
                lastLogin = System.currentTimeMillis()
            )
        } else {
            null
        }
    }

    override suspend fun logOut() {
        try {
            auth.signOut()

            val googleClient = getGoogleSignInClient(context)
            googleClient.signOut().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}