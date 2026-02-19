package com.example.financeaudit.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.financeaudit.presentation.ui.components.AppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    rootNavController: NavController,
) {
    Scaffold(
        topBar = {AppBar(titleContent = { Text("Chat") }, onBackClick = {rootNavController.popBackStack()})}
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            Text("Chat Screen")
        }

    }

}

