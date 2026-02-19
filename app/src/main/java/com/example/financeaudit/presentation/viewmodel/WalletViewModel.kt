package com.example.financeaudit.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeaudit.domain.model.Wallet
import com.example.financeaudit.domain.repository.AuthRepository
import com.example.financeaudit.domain.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class WalletState {
    object Loading : WalletState()
    data class Success(val wallets: List<Wallet>) : WalletState()
    data class Error(val message: String) : WalletState()
}

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val walletRepository: WalletRepository
): ViewModel() {
    private val _state = MutableStateFlow<WalletState>(WalletState.Loading)
    val state: StateFlow<WalletState> = _state.asStateFlow()

    init {
        subscribeToWallets()
    }

    private fun subscribeToWallets() {
        viewModelScope.launch {
            _state.value = WalletState.Loading
            val currentUser = authRepository.getCurrentUser()

            if (currentUser != null) {
                walletRepository.getWalletsRealtime(currentUser.uid)
                    .collect { wallets ->
                        _state.value = WalletState.Success(wallets)
                    }
            } else {
                _state.value = WalletState.Error("User not logged in")
            }
        }
    }

    fun deleteWallet(walletId: String) {
        viewModelScope.launch {
            walletRepository.deleteWallet(walletId)
                .onFailure { e ->
                    _state.value = WalletState.Error(e.message ?: "Failed to delete")
                }
        }
    }
}