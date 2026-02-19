package com.example.financeaudit.presentation.view

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.financeaudit.presentation.navigation.Screen
import com.example.financeaudit.presentation.viewmodel.SplashViewModel

@Composable
fun SplashScreen(
    rootNavController: NavController,
    viewModel: SplashViewModel = hiltViewModel(),
    pendingImageUri: Uri? = null
) {
    val destination by viewModel.startDestination.collectAsState()

    LaunchedEffect(destination) {
        destination?.let { route ->
            if (route == Screen.Main.route) {

                if (pendingImageUri != null) {
                    val encodedUri = Uri.encode(pendingImageUri.toString())

                    rootNavController.navigate("scan_result/$encodedUri") {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                } else {
                    rootNavController.navigate(route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }

            } else {
                rootNavController.navigate(route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}