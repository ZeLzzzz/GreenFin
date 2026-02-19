package com.example.financeaudit.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.financeaudit.R
import com.example.financeaudit.presentation.navigation.Screen
import com.example.financeaudit.presentation.ui.components.InputC
import com.example.financeaudit.presentation.ui.components.SelectC
import com.example.financeaudit.presentation.ui.theme.FinanceAuditTheme
import com.example.financeaudit.presentation.viewmodel.InitialSetupState
import com.example.financeaudit.presentation.viewmodel.InitialSetupViewModel
import com.example.financeaudit.presentation.ui.components.ButtonC

@Composable
fun InitialSetupScreen(
    rootNavController: NavController,
    viewModel: InitialSetupViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isComplete) {
        if (state.isComplete) {
            rootNavController.navigate(Screen.Main.route) {
                popUpTo(Screen.InitialSetup.route) { inclusive = true }
            }
        }
    }

    InitialSetupContent(
        state = state,
        onNameChange = viewModel::onNameChange,
        onNextStep = viewModel::nextStep,
        onWalletNameChange = viewModel::onWalletNameChange,
        onWalletAmountChange = viewModel::onWalletAmountChange,
        onWalletTypeChange = viewModel::onWalletTypeChange,
        onFinish = viewModel::finishSetup
    )
}

@Composable
fun InitialSetupContent(
    state: InitialSetupState,
    onNameChange: (String) -> Unit,
    onNextStep: () -> Unit,
    onWalletNameChange: (String) -> Unit,
    onWalletAmountChange: (String) -> Unit,
    onWalletTypeChange: (String) -> Unit,
    onFinish: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (state.step >= 2) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surface
                            )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                when (state.step) {
                    1 -> Step1Content(
                        name = state.name,
                        onNameChange = onNameChange,
                        onContinue = onNextStep
                    )
                    2 -> Step2Content(
                        walletName = state.walletName,
                        walletAmount = state.walletAmount,
                        walletType = state.walletType,
                        onWalletNameChange = onWalletNameChange,
                        onWalletAmountChange = onWalletAmountChange,
                        onWalletTypeChange = onWalletTypeChange,
                        onFinish = onFinish,
                        isLoading = state.isLoading
                    )
                }
            }
        }
    }
}

@Composable
fun Step1Content(name: String, onNameChange: (String) -> Unit, onContinue: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "STEP 1 OF 2",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "What should we call you?",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "This will be displayed on your profile and dashboard.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(32.dp))

        InputC(
            value = name,
            onValueChange = onNameChange,
            placeholder = "Enter your name"
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                )
                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.check_circle),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "We use your name to personalize your finance reports and greet you when you open the app.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                lineHeight = 20.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        ButtonC(onClick = onContinue) {
            Text(text = "Continue", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(painter = painterResource(R.drawable.arrow_forward_ios), contentDescription = null, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun Step2Content(
    walletName: String,
    walletAmount: String,
    walletType: String,
    onWalletNameChange: (String) -> Unit,
    onWalletAmountChange: (String) -> Unit,
    onWalletTypeChange: (String) -> Unit,
    onFinish: () -> Unit,
    isLoading: Boolean
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "STEP 2 OF 2",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Set up your Main Wallet",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Enter your primary account details to start tracking your wealth.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        SelectC(
            label = "Wallet Type",
            selectedOption = walletType,
            onOptionSelected = onWalletTypeChange,
            options = listOf("Bank", "E-Wallet"),
            placeholder = "Select Type"
        )

        Spacer(modifier = Modifier.height(16.dp))

        InputC(
            label = "Account Name",
            value = walletName,
            onValueChange = onWalletNameChange,
            placeholder = "e.g. BCA, Gopay"
        )

        Spacer(modifier = Modifier.height(16.dp))

        InputC(
            label = "Current Balance",
            value = walletAmount,
            onValueChange = onWalletAmountChange,
            placeholder = "Rp 0",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.weight(1f))

        ButtonC(
            onClick = onFinish,
            enabled = !isLoading,
            content = {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text(text = "Finish", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(painter = painterResource(R.drawable.check_circle), contentDescription = null, modifier = Modifier.size(20.dp))
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewInitialSetupStep2() {
    FinanceAuditTheme(darkTheme = true) {
        InitialSetupContent(
            state = InitialSetupState(
                step = 2,
                walletName = "BCA",
                walletAmount = "1500000",
                walletType = "Bank"
            ),
            onNameChange = {},
            onNextStep = {},
            onWalletNameChange = {},
            onWalletAmountChange = {},
            onWalletTypeChange = {},
            onFinish = {}
        )
    }
}