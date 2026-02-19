@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.financeaudit.presentation.view.main

import BankCard
import CardItem
import TransactionCard
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.financeaudit.R
import com.example.financeaudit.presentation.ui.components.AppBar
import com.example.financeaudit.presentation.ui.components.BottomNavItem
import com.example.financeaudit.presentation.util.formatRupiah
import com.example.financeaudit.presentation.util.shimmerEffect
import com.example.financeaudit.presentation.viewmodel.DashboardState
import com.example.financeaudit.presentation.viewmodel.DashboardViewModel
import com.example.financeaudit.presentation.ui.components.ButtonC
import com.example.financeaudit.presentation.ui.components.ButtonVariant

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    bottomNavController: NavController,
    onIncomeButtonClick: () -> Unit,
    onExpenseButtonClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    val isLoading = uiState is DashboardState.Loading
    val successState = uiState as? DashboardState.Success
    val user = successState?.user
    val wallets = successState?.wallets ?: emptyList()
    val transactions = successState?.recentTransactions ?: emptyList()

    val totalBalance = wallets.sumOf { it.balance }
    Scaffold(
        topBar = {
            AppBar(
                titleContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 15.dp)
                    ) {
                        if (isLoading) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .shimmerEffect()
                            )
                        } else {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(user?.photoUrl)
                                    .crossfade(true)
                                    .build(),
                                placeholder = painterResource(R.drawable.person),
                                error = painterResource(R.drawable.person),
                                contentDescription = "Profile",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Welcome,",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                            if (isLoading) {
                                Box(
                                    modifier = Modifier
                                        .width(120.dp)
                                        .height(20.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .shimmerEffect()
                                )
                            } else {
                                user?.name?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                        }
                    }
                },

            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(
                        text = "Total Balance",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .width(200.dp)
                                .height(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .shimmerEffect()
                        )
                    } else {
                        Text(
                            text = formatRupiah(totalBalance),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp).padding(16.dp).shimmerEffect())
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Wallets", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
                        ButtonC(variant = ButtonVariant.Ghost, onClick = {
                            bottomNavController.navigate(BottomNavItem.Wallet.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        } , fullWidth = false, height = 40.dp) {
                            Text(text = "View all", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(wallets) { wallet ->
                            BankCard(
                                CardItem(
                                    balance = formatRupiah(wallet.balance),
                                    name = wallet.name,
                                )
                            )
                        }
                    }
                }

                if (!isLoading && wallets.isEmpty()) {
                    Text(
                        text = "No wallets found.",
                        modifier = Modifier.padding(20.dp),
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ButtonC(
                            variant = ButtonVariant.Icon,
                            onClick = onIncomeButtonClick,
                            fullWidth = false,
                            size = 60.dp,
                            contentColor = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            Icon(painter = painterResource(id = R.drawable.vertical_align_bottom), contentDescription = null)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Income", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ButtonC(
                            variant = ButtonVariant.Icon,
                            onClick = onExpenseButtonClick,
                            fullWidth = false,
                            size = 60.dp,
                            contentColor = Color.Red,
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            Icon(painter = painterResource(id = R.drawable.vertical_align_top), contentDescription = null)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Expense", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }

                }

                Spacer(modifier = Modifier.height(18.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Recent Transactions", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
                    ButtonC(variant = ButtonVariant.Ghost, onClick = {}, fullWidth = false, height = 40.dp) {
                        Text(text = "View all", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

            }

            if (transactions.isEmpty() && !isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No transactions yet", color = Color.Gray)
                    }
                }
            } else {
                items(transactions) { transaction ->
                    TransactionCard(transaction = transaction)
                }
            }
        }
    }
}