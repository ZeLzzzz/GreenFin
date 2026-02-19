package com.example.financeaudit.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeaudit.domain.model.Transaction
import com.example.financeaudit.domain.repository.AuthRepository
import com.example.financeaudit.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TransactionState {
    object Loading : TransactionState()
    data class Success(val transactions: List<Transaction>) : TransactionState()
    data class Error(val message: String) : TransactionState()
}

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow<TransactionState>(TransactionState.Loading)
    val state = _state.asStateFlow()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                transactionRepository.getTransactionsRealtime(user.uid)
                    .onStart { _state.value = TransactionState.Loading }
                    .catch { e -> _state.value = TransactionState.Error(e.message ?: "Unknown Error") }
                    .collect { transactions ->
                        val sorted = transactions.sortedByDescending { it.date }
                        _state.value = TransactionState.Success(sorted)
                    }
            } else {
                _state.value = TransactionState.Error("User not logged in")
            }
        }
    }
}