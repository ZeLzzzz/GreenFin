package com.example.financeaudit.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeaudit.domain.model.Wallet
import com.example.financeaudit.domain.repository.AuthRepository
import com.example.financeaudit.domain.repository.WalletRepository
import com.example.financeaudit.presentation.util.parseRupiahInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddWalletState(
    val name: String = "",
    val amount: String = "",
    val type: String = "Bank",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddWalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddWalletState())
    val state = _state.asStateFlow()

    fun onNameChange(value: String) {
        _state.update { it.copy(name = value) }
    }

    fun onAmountChange(value: String) {
        if (value.isEmpty() || value.all { it.isDigit() }) {
            _state.update { it.copy(amount = value) }
        }
    }

    fun onTypeChange(value: String) {
        _state.update { it.copy(type = value) }
    }

    fun saveWallet() {
        val currentState = _state.value

        if (currentState.name.isBlank() || currentState.amount.isBlank()) {
            _state.update { it.copy(error = "Please fill all fields") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val user = authRepository.getCurrentUser()
            if (user != null) {
                try {
                    val newWallet = Wallet(
                        userId = user.uid,
                        name = currentState.name,
                        balance = parseRupiahInput(currentState.amount),
                        type = currentState.type.uppercase(),
                    )

                    val result = walletRepository.createWallets(listOf(newWallet))

                    result.onSuccess {
                        _state.update { it.copy(isLoading = false, isSuccess = true) }
                    }
                    result.onFailure { e ->
                        _state.update { it.copy(isLoading = false, error = e.message) }
                    }
                } catch (e: Exception) {
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
            } else {
                _state.update { it.copy(isLoading = false, error = "User not found") }
            }
        }
    }
}