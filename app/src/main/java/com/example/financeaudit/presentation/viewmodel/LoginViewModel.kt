package com.example.financeaudit.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeaudit.data.local.SessionManager
import com.example.financeaudit.domain.repository.AuthRepository
import com.example.financeaudit.domain.repository.UserRepository
import com.example.financeaudit.domain.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        object NavigateToDashboard : LoginState()
        object NavigateToInitialSetup : LoginState()
        data class Error(val message: String) : LoginState()
    }

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val walletRepository: WalletRepository
): ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState  = _loginState.asStateFlow()

    fun handleGoogleLogin(idToken: String) {
        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            val loginResult = authRepository.signInWithGoogle(idToken)

            loginResult.fold(
                onSuccess = { user ->
                    try {
                        userRepository.saveUser(user)
                        sessionManager.saveLoginTime()

                        val walletResult = walletRepository.getUserWallets(user.uid)

                        walletResult.fold(
                            onSuccess = { wallets ->
                                if (wallets.isNotEmpty()) {
                                    _loginState.value = LoginState.NavigateToDashboard
                                } else {
                                    _loginState.value = LoginState.NavigateToInitialSetup
                                }
                            },
                            onFailure = {
                                _loginState.value = LoginState.NavigateToInitialSetup
                            }
                        )
                    } catch (e: Exception) {
                        _loginState.value = LoginState.Error("Failed to save user data")
                    }
                },
                onFailure = { exception ->
                    _loginState.value = LoginState.Error(exception.message ?: "Login Failed")
                }
            )
        }
    }
}