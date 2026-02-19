package com.example.financeaudit.data.repository

import com.example.financeaudit.domain.model.Transaction
import com.example.financeaudit.domain.repository.TransactionRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : TransactionRepository {

    override suspend fun addTransaction(transaction: Transaction): Result<Unit> {
        return try {
            val transactionRef = firestore.collection("transactions").document()

            val walletRef = firestore.collection("wallets").document(transaction.walletId)

            val balanceChange = if (transaction.type == "EXPENSE") {
                -transaction.amount
            } else {
                transaction.amount
            }

            firestore.runBatch { batch ->
                val finalTransaction = transaction.copy(id = transactionRef.id)
                batch.set(transactionRef, finalTransaction)

                batch.update(walletRef, "balance", FieldValue.increment(balanceChange))
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getTransactionsRealtime(userId: String): Flow<List<Transaction>> = callbackFlow {
        val listener = firestore.collection("transactions")
            .whereEqualTo("userId", userId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val transactions = snapshot.toObjects(Transaction::class.java)
                    trySend(transactions)
                }
            }

        awaitClose { listener.remove() }
    }
}