package com.example.financeaudit.domain.repository

import com.example.financeaudit.domain.model.Wallet
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    suspend fun createWallets(wallets: List<Wallet>): Result<Boolean>
    suspend fun getUserWallets(userId: String): Result<List<Wallet>>
    fun getWalletsRealtime(userId: String): Flow<List<Wallet>>
    suspend fun deleteWallet(walletId: String): Result<Unit>
}