package com.example.financeaudit.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeaudit.domain.model.Transaction
import com.example.financeaudit.domain.model.User
import com.example.financeaudit.domain.model.Wallet
import com.example.financeaudit.domain.repository.AuthRepository
import com.example.financeaudit.domain.repository.TransactionRepository
import com.example.financeaudit.domain.repository.UserRepository
import com.example.financeaudit.domain.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DashboardState {
    object Loading : DashboardState()
    data class Success(
        val user: User,
        val wallets: List<Wallet>,
        val recentTransactions: List<Transaction>
    ) : DashboardState()
    data class Error(val message: String) : DashboardState()
}
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val walletRepository: WalletRepository,
    private val transactionRepository: TransactionRepository
): ViewModel() {
    private val _uiState = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()

    init {
        fetchDashboardData()
    }
    private fun fetchDashboardData() {
        viewModelScope.launch {
            _uiState.value = DashboardState.Loading

            val currentUser = authRepository.getCurrentUser()

            if (currentUser != null) {
                val userResult = userRepository.getUserProfile(currentUser.uid)
                val user = userResult.getOrNull()

                if (user != null) {
                    val walletsFlow = walletRepository.getWalletsRealtime(currentUser.uid)
                    val transactionsFlow = transactionRepository.getTransactionsRealtime(currentUser.uid)

                    combine(walletsFlow, transactionsFlow) { wallets, transactions ->
                        val recent = transactions.take(5)

                        DashboardState.Success(
                            user = user,
                            wallets = wallets,
                            recentTransactions = recent
                        )
                    }.collect { state ->
                        _uiState.value = state
                    }
                    // ---------------------------
                } else {
                    _uiState.value = DashboardState.Error("Failed to load user profile")
                }
            } else {
                _uiState.value = DashboardState.Error("User session not found")
            }
        }
    }
}