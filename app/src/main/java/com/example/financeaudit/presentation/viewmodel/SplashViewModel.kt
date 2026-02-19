package com.example.financeaudit.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeaudit.data.local.SessionManager
import com.example.financeaudit.domain.repository.AuthRepository
import com.example.financeaudit.domain.repository.WalletRepository
import com.example.financeaudit.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val walletRepository: WalletRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination = _startDestination.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            delay(2000)

            if (authRepository.isUserLoggedIn()) {
                if (sessionManager.isSessionExpired()) {
                    authRepository.logOut()
                    sessionManager.clearSession()
                    _startDestination.value = Screen.Login.route
                } else {
                    checkUserWallet()
                }
            } else {
                _startDestination.value = Screen.Login.route
            }
        }
    }
    private suspend fun checkUserWallet() {
        val user = authRepository.getCurrentUser()
        if (user != null) {
            Log.d("SPLASH_DEBUG", "User ID saat ini: ${user.uid}") // Cek UID
            val result = walletRepository.getUserWallets(user.uid)

            result.fold(
                onSuccess = { wallets ->
                    Log.d("SPLASH_DEBUG", "Sukses ambil wallet. Jumlah: ${wallets.size}") // Cek jumlah
                    if (wallets.isNotEmpty()) {
                        _startDestination.value = Screen.Main.route
                    } else {
                        Log.d("SPLASH_DEBUG", "Wallet kosong -> Navigasi ke INITIAL SETUP")
                        _startDestination.value = Screen.InitialSetup.route
                    }
                },
                onFailure = { e ->
                    Log.e("SPLASH_DEBUG", "Gagal ambil wallet: ${e.message}") // Cek Error
                    _startDestination.value = Screen.InitialSetup.route
                }
            )
        } else {
            Log.d("SPLASH_DEBUG", "User null -> Login")
            _startDestination.value = Screen.Login.route
        }
    }
}