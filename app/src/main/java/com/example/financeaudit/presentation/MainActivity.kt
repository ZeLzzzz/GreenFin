package com.example.financeaudit.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.financeaudit.presentation.navigation.Screen
import com.example.financeaudit.presentation.ui.theme.FinanceAuditTheme
import com.example.financeaudit.presentation.view.*
import com.example.financeaudit.presentation.view.auth.LoginScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FinanceAuditTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val rootNavController = rememberNavController()

                    var sharedImageUri by remember { mutableStateOf<Uri?>(null) }

                    LaunchedEffect(Unit) {
                        val intent = intent
                        val action = intent.action
                        val type = intent.type

                        if (Intent.ACTION_SEND == action && type != null) {
                            if (type.startsWith("image/")) {
                                (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let { uri ->
                                    sharedImageUri = uri
                                }
                            }
                        }
                    }

                    NavHost(
                        navController = rootNavController,
                        startDestination = Screen.Splash.route
                    ) {
                        composable(route = Screen.Login.route) {
                            LoginScreen(rootNavController = rootNavController)
                        }

                        composable(route = Screen.Splash.route) {
                            SplashScreen(
                                rootNavController = rootNavController,
                                pendingImageUri = sharedImageUri
                            )
                        }

                        composable(route = Screen.Main.route) {
                            MainScreen(rootNavController = rootNavController)
                        }

                        composable(route = Screen.InitialSetup.route) {
                            InitialSetupScreen(rootNavController = rootNavController)
                        }

                        composable(route = Screen.AddWallet.route) {
                            AddWalletScreen(rootNavController = rootNavController)
                        }

                        composable(route = Screen.Income.route) {
                            IncomeScreen(rootNavController = rootNavController)
                        }

                        composable(route = Screen.Expense.route) {
                            ExpenseScreen(rootNavController = rootNavController)
                        }

                        composable(route = Screen.Chat.route) {
                            ChatScreen(rootNavController = rootNavController)
                        }

                        composable(
                            route = "scan_result/{uri}",
                            arguments = listOf(navArgument("uri") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val uriString = backStackEntry.arguments?.getString("uri")
                            val uri = Uri.parse(uriString)

                            ScanResultScreen(
                                imageUri = uri
                            )
                        }
                    }
                }
            }
        }
    }
}