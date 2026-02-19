package com.example.financeaudit.presentation.view.main

import BankCard
import CardItem
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.financeaudit.R
import com.example.financeaudit.presentation.ui.components.AppBar
import com.example.financeaudit.presentation.util.formatRupiah
import com.example.financeaudit.presentation.util.shimmerEffect
import com.example.financeaudit.presentation.viewmodel.WalletState
import com.example.financeaudit.presentation.viewmodel.WalletViewModel
import com.example.financeaudit.presentation.ui.components.ButtonC
import com.example.financeaudit.presentation.ui.components.ButtonVariant

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WalletScreen(
    viewModel: WalletViewModel = hiltViewModel(),
    onAddWalletClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            AppBar(
                titleContent = { Text(text = "My Wallets") },
                actions = {
                    ButtonC(
                        variant = ButtonVariant.Icon,
                        onClick = onAddWalletClick,
                        size = 50.dp
                    ) {
                        Icon(painter = painterResource(id = R.drawable.add), contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val currentState = state) {
                is WalletState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp).padding(16.dp).shimmerEffect())
                }
                is WalletState.Error -> {
                    Text(
                        text = currentState.message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is WalletState.Success -> {
                    if (currentState.wallets.isEmpty()) {
                        Text(
                            text = "No wallets found",
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            items(items = currentState.wallets, key = { it.id }) { wallet ->
                                val dismissState = rememberSwipeToDismissBoxState(
                                    positionalThreshold = { totalDistance ->
                                        totalDistance * 1f
                                    }
                                )

                                LaunchedEffect(dismissState.currentValue) {
                                    if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                                        viewModel.deleteWallet(wallet.id)
                                    }
                                }
                                SwipeToDismissBox(
                                    state = dismissState,
                                    enableDismissFromStartToEnd = false,
                                    enableDismissFromEndToStart = true,
                                    backgroundContent = {
                                        DeleteBackground(dismissState)
                                    },
                                    content = {
                                        BankCard(
                                            fillMaxWidth = true,
                                            card = CardItem(
                                                balance = formatRupiah(wallet.balance),
                                                name = wallet.name
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteBackground(swipeDismissState: SwipeToDismissBoxState) {
    val color = if (swipeDismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        Color.Transparent
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color, RoundedCornerShape(18.dp))
            .padding(16.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        if (swipeDismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
            Icon(
                painter = painterResource(R.drawable.delete),
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}