package com.example.financeaudit.data.repository

import com.example.financeaudit.domain.model.Wallet
import com.example.financeaudit.domain.repository.WalletRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class WalletRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): WalletRepository {
    override suspend fun createWallets(wallets: List<Wallet>): Result<Boolean> {
        return try {
            val batch = firestore.batch()
            val walletsCollection = firestore.collection("wallets")

            wallets.forEach { wallet ->
                val docRef = walletsCollection.document()

                val walletWithId = wallet.copy(id = docRef.id)

                batch.set(docRef, walletWithId)
            }

            batch.commit().await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getWalletsRealtime(userId: String): Flow<List<Wallet>> = callbackFlow {
        val listener = firestore.collection("wallets")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val wallets = snapshot.toObjects(Wallet::class.java)
                    trySend(wallets)
                }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getUserWallets(userId: String): Result<List<Wallet>> {
        return try {
            val snapshot = firestore.collection("wallets")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val wallets = snapshot.toObjects(Wallet::class.java)

            Result.success(wallets)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteWallet(walletId: String): Result<Unit> {
        return try {
            firestore.collection("wallets").document(walletId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}