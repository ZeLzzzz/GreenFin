package com.example.financeaudit.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeaudit.domain.model.Wallet
import com.example.financeaudit.domain.repository.AuthRepository
import com.example.financeaudit.domain.repository.UserRepository
import com.example.financeaudit.domain.repository.WalletRepository
import com.example.financeaudit.presentation.util.parseRupiahInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class InitialSetupState(
    val step: Int = 1,
    val name: String = "",
    val walletName: String = "",
    val walletAmount: String = "",
    val walletType: String = "Bank",

    val isLoading: Boolean = false,
    val error: String? = null,
    val isComplete: Boolean = false
)

@HiltViewModel
class InitialSetupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val walletRepository: WalletRepository
) : ViewModel() {
    private val _state = MutableStateFlow(InitialSetupState())
    val state = _state.asStateFlow()

    init {
        fetchCurrentUserName()
    }

    private fun fetchCurrentUserName() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            if (currentUser != null) {
                _state.update { it.copy(name = currentUser.name) }
            }
        }
    }

    fun onNameChange(newName: String) {
        _state.update { it.copy(name = newName) }
    }

    fun onWalletNameChange(value: String) {
        _state.update { it.copy(walletName = value) }
    }

    fun onWalletAmountChange(value: String) {
        if (value.isEmpty() || value.all { it.isDigit() }) {
            _state.update { it.copy(walletAmount = value) }
        }
    }

    fun onWalletTypeChange(value: String) {
        _state.update { it.copy(walletType = value) }
    }

    fun nextStep() {
        if (_state.value.name.isNotBlank()) {
            _state.update { it.copy(step = 2) }
        }
    }

    fun finishSetup() {
        val currentState = _state.value
        if (currentState.isLoading) return

        if (currentState.walletName.isBlank() || currentState.walletAmount.isBlank()) {
            _state.update { it.copy(error = "Please fill all fields") }
            return
        }

        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val currentUser = authRepository.getCurrentUser() ?: throw Exception("User not found")

                val updatedUser = currentUser.copy(name = currentState.name)
                userRepository.saveUser(updatedUser)

                val mainWallet = Wallet(
                    userId = currentUser.uid,
                    name = currentState.walletName,
                    balance = parseRupiahInput(currentState.walletAmount),
                    type = currentState.walletType.uppercase(),
                )

                val walletResult = walletRepository.createWallets(listOf(mainWallet))

                if (walletResult.isSuccess) {
                    _state.update { it.copy(isLoading = false, isComplete = true) }
                } else {
                    throw Exception("Failed to create wallet")
                }

            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }
}