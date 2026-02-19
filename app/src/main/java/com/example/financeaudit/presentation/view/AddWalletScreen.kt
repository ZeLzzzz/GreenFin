package com.example.financeaudit.presentation.view

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.financeaudit.R
import com.example.financeaudit.presentation.ui.components.AppBar
import com.example.financeaudit.presentation.ui.components.ButtonC
import com.example.financeaudit.presentation.ui.components.InputC
import com.example.financeaudit.presentation.ui.components.SelectC
import com.example.financeaudit.presentation.viewmodel.AddWalletViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddWalletScreen(
    rootNavController: NavController,
    viewModel: AddWalletViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            Toast.makeText(context, "Wallet added successfully!", Toast.LENGTH_SHORT).show()
            rootNavController.popBackStack()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            AppBar(
                titleContent = { },
                onBackClick = { rootNavController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Add New Wallet",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add another account to track your separate savings or expenses.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            SelectC(
                label = "Wallet Type",
                selectedOption = state.type,
                onOptionSelected = viewModel::onTypeChange,
                options = listOf("Bank", "E-Wallet", "Cash", "Investment"),
                placeholder = "Select Type"
            )

            Spacer(modifier = Modifier.height(16.dp))

            InputC(
                label = "Account Name",
                value = state.name,
                onValueChange = viewModel::onNameChange,
                placeholder = "e.g. BNI, BCA, OVO"
            )

            Spacer(modifier = Modifier.height(16.dp))

            InputC(
                label = "Current Balance",
                value = state.amount,
                onValueChange = viewModel::onAmountChange,
                placeholder = "Rp 0",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.weight(1f))

            ButtonC(
                onClick = viewModel::saveWallet,
                enabled = !state.isLoading,
                content = {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = "Save Wallet",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(R.drawable.check_circle),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        }
    }
}