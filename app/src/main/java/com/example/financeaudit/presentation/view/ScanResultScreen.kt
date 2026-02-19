package com.example.financeaudit.presentation.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.financeaudit.domain.model.CategoryData
import com.example.financeaudit.presentation.MainActivity
import com.example.financeaudit.presentation.ui.components.AppBar
import com.example.financeaudit.presentation.ui.components.ButtonC
import com.example.financeaudit.presentation.ui.components.InputC
import com.example.financeaudit.presentation.ui.components.SelectC
import com.example.financeaudit.presentation.viewmodel.ScanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanResultScreen(
    navController: NavController,
    imageUri: Uri,
    viewModel: ScanViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        android.util.Log.d("ScanResultScreen", "Screen Opened with URI: $imageUri")
        if (!state.isAnalyzing && state.amount.isEmpty()) {
            viewModel.analyzeImage(imageUri)
        }
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            Toast.makeText(context, "Transaction Saved!", Toast.LENGTH_SHORT).show()

            val packageManager = context.packageManager
            val intent = packageManager.getLaunchIntentForPackage(context.packageName)

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

                context.startActivity(intent)

                (context as? Activity)?.finish()
            }
        }
    }

    Scaffold(
        topBar = { AppBar(titleContent = { Text("Scan Result") }, onBackClick = { navController.popBackStack() }) }
    ) { innerPadding ->

        if (state.isAnalyzing) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(16.dp))
                    Text("AI is reading your receipt...")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Gray.copy(alpha = 0.1f))
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(state.detectedImageUri),
                        contentDescription = "Receipt",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Detected Type: ${state.type}",
                    style = MaterialTheme.typography.labelLarge,
                    color = if(state.type == "INCOME") Color(0xFF4CAF50) else Color(0xFFE53935)
                )

                Spacer(Modifier.height(16.dp))

                InputC(
                    label = "Amount",
                    value = state.amount,
                    onValueChange = viewModel::onAmountChange,
                    placeholder = "0"
                )

                Spacer(Modifier.height(16.dp))

                val currentWalletName = state.availableWallets.find { it.id == state.selectedWalletId }?.name ?: ""
                SelectC(
                    label = "Wallet",
                    selectedOption = currentWalletName,
                    onOptionSelected = viewModel::onWalletChange,
                    options = state.walletNames
                )

                Spacer(Modifier.height(16.dp))

                val currentCategoryName = CategoryData.allCategories.find { it.id == state.selectedCategoryId }?.name ?: ""
                SelectC(
                    label = "Category",
                    selectedOption = currentCategoryName,
                    onOptionSelected = viewModel::onCategoryChange,
                    options = state.categoryNames
                )

                Spacer(Modifier.height(16.dp))

                InputC(
                    label = "Note",
                    value = state.note,
                    onValueChange = viewModel::onNoteChange
                )

                Spacer(Modifier.height(32.dp))

                ButtonC(
                    onClick = viewModel::saveTransaction,
                    enabled = !state.isLoading,
                    content = {
                        if (state.isLoading) CircularProgressIndicator(color = Color.White)
                        else Text("Confirm & Save", fontWeight = FontWeight.Bold)
                    }
                )
            }
        }
    }
}