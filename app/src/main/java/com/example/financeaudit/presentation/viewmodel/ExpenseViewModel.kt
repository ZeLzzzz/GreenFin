package com.example.financeaudit.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeaudit.domain.model.CategoryData
import com.example.financeaudit.domain.model.Transaction
import com.example.financeaudit.domain.model.Wallet
import com.example.financeaudit.domain.repository.AuthRepository
import com.example.financeaudit.domain.repository.TransactionRepository
import com.example.financeaudit.domain.repository.WalletRepository
import com.example.financeaudit.presentation.util.parseRupiahInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class ExpenseState(
    val amount: String = "",
    val note: String = "",
    val selectedCategoryName: String = "",
    val selectedWalletName: String = "",

    val availableWallets: List<Wallet> = emptyList(),
    val walletNames: List<String> = emptyList(),
    val categoryNames: List<String> = CategoryData.expenseCategories.map { it.name },

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val walletRepository: WalletRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ExpenseState())
    val state = _state.asStateFlow()

    init {
        loadWallets()
    }

    private fun loadWallets() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                walletRepository.getUserWallets(user.uid)
                    .onSuccess { wallets ->
                        _state.update {
                            it.copy(
                                availableWallets = wallets,
                                walletNames = wallets.map { w -> w.name }
                            )
                        }
                    }
            }
        }
    }

    fun onAmountChange(value: String) {
        if (value.isEmpty() || value.all { it.isDigit() }) {
            _state.update { it.copy(amount = value) }
        }
    }

    fun onNoteChange(value: String) {
        _state.update { it.copy(note = value) }
    }

    fun onCategoryChange(name: String) {
        _state.update { it.copy(selectedCategoryName = name) }
    }

    fun onWalletChange(name: String) {
        _state.update { it.copy(selectedWalletName = name) }
    }

    fun saveExpense() {
        val currentState = _state.value

        if (currentState.amount.isBlank() || currentState.selectedWalletName.isBlank() || currentState.selectedCategoryName.isBlank()) {
            _state.update { it.copy(error = "Please fill all required fields") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val user = authRepository.getCurrentUser() ?: throw Exception("User not found")

                val selectedWallet = currentState.availableWallets.find { it.name == currentState.selectedWalletName }
                val selectedCategory = CategoryData.expenseCategories.find { it.name == currentState.selectedCategoryName }

                if (selectedWallet == null) throw Exception("Invalid Wallet")

                val expenseAmount = parseRupiahInput(currentState.amount)

                if (expenseAmount > selectedWallet.balance) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Insufficient balance! Your ${selectedWallet.name} only has Rp ${selectedWallet.balance}"
                        )
                    }
                    return@launch
                }

                val transaction = Transaction(
                    userId = user.uid,
                    walletId = selectedWallet.id,
                    amount = expenseAmount,
                    type = "EXPENSE",
                    category = selectedCategory?.id ?: "other_expense",
                    note = currentState.note,
                    date = Date()
                )

                transactionRepository.addTransaction(transaction)
                    .onSuccess {
                        _state.update { it.copy(isLoading = false, isSuccess = true) }
                    }
                    .onFailure { e ->
                        _state.update { it.copy(isLoading = false, error = e.message) }
                    }

            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}