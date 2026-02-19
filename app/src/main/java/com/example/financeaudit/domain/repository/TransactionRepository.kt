package com.example.financeaudit.domain.repository

import com.example.financeaudit.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun addTransaction(transaction: Transaction): Result<Unit>

    fun getTransactionsRealtime(userId: String): Flow<List<Transaction>>
}