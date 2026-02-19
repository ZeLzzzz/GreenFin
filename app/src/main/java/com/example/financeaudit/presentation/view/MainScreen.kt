package com.example.financeaudit.presentation.view

import com.example.financeaudit.presentation.view.main.WalletScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.financeaudit.presentation.navigation.Screen
import com.example.financeaudit.presentation.ui.components.BottomNavBar
import com.example.financeaudit.presentation.ui.components.BottomNavItem
import com.example.financeaudit.presentation.view.main.DashboardScreen
import com.example.financeaudit.presentation.view.main.TransactionScreen

@Composable fun StatsScreen() { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Stats Screen") } }

@Composable
fun MainScreen(
    rootNavController: NavController
) {
    val bottomNavController = rememberNavController()

    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: BottomNavItem.Home.route

    Scaffold(
        bottomBar = {
            if (currentRoute != BottomNavItem.Scan.route) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        bottomNavController.navigate(route) {
                            popUpTo(bottomNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onScanClick = {
                        bottomNavController.navigate(BottomNavItem.Scan.route)
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) { DashboardScreen(
                onIncomeButtonClick = {
                    rootNavController.navigate(Screen.Income.route)
                },
                onExpenseButtonClick = {
                    rootNavController.navigate(Screen.Expense.route)
                },
                bottomNavController = bottomNavController
            ) }
            composable(BottomNavItem.Stats.route) { StatsScreen() }
            composable(BottomNavItem.Wallet.route) { WalletScreen(
                onAddWalletClick = {
                    rootNavController.navigate(Screen.AddWallet.route)
                }
            )}
            composable(BottomNavItem.Profile.route) { TransactionScreen() }
        }
    }
}