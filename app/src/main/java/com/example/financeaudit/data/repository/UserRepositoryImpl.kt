package com.example.financeaudit.data.repository

import com.example.financeaudit.domain.model.User
import com.example.financeaudit.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {
    override suspend fun saveUser(user: User): Result<Boolean> {
           return try {
                firestore.collection("users")
                    .document(user.uid)
                    .set(user, SetOptions.merge())
                    .await()

                Result.success(true)
           } catch (e: Exception) {
               Result.failure(e)
           }
    }

    override suspend fun getUserProfile(userId: String): Result<User> {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            val user = snapshot.toObject(User::class.java)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}