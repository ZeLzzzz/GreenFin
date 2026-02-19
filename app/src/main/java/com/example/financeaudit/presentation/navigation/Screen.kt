package com.example.financeaudit.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object Main : Screen("main_screen")
    object Splash : Screen("splash_screen")
    object InitialSetup : Screen("initial_setup_screen")
    object AddWallet : Screen("add_wallet_screen")
    object Income : Screen("income_screen")
    object Expense : Screen("expense_screen")
    object Chat: Screen("chat_screen")
}