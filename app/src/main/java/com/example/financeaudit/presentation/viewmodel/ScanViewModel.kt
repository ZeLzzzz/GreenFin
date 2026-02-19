package com.example.financeaudit.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeaudit.data.remote.*
import com.example.financeaudit.domain.model.CategoryData
import com.example.financeaudit.domain.model.Transaction
import com.example.financeaudit.domain.model.Wallet
import com.example.financeaudit.domain.repository.AuthRepository
import com.example.financeaudit.domain.repository.TransactionRepository
import com.example.financeaudit.domain.repository.WalletRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class ScanResultState(
    val isLoading: Boolean = false,
    val isAnalyzing: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false,

    val detectedImageUri: Uri? = null,
    val amount: String = "",
    val note: String = "",
    val type: String = "EXPENSE",

    val selectedWalletId: String = "",
    val selectedCategoryId: String = "",

    val availableWallets: List<Wallet> = emptyList(),
    val walletNames: List<String> = emptyList(),
    val categoryNames: List<String> = emptyList()
)

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val groqApi: GroqApi,
    private val walletRepository: WalletRepository,
    private val authRepository: AuthRepository,
    private val transactionRepository: TransactionRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(ScanResultState())
    val state = _state.asStateFlow()
    val GROQ_API_KEY = "your-api-key"

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                walletRepository.getUserWallets(user.uid).onSuccess { wallets ->
                    _state.update {
                        it.copy(
                            availableWallets = wallets,
                            walletNames = wallets.map { w -> w.name },
                            categoryNames = CategoryData.allCategories.map { c -> c.name }
                        )
                    }
                }
            }
        }
    }

    fun analyzeImage(uri: Uri) {
        Log.d("ScanViewModel", "analyzeImage CALLED with URI: $uri")
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isAnalyzing = true, detectedImageUri = uri, error = null) }

            try {
                val base64 = encodeImageToBase64(context.contentResolver, uri) ?: throw Exception("Image error")

                val walletListString = _state.value.walletNames.joinToString(", ")
                val categoryListString = CategoryData.allCategories.joinToString(", ") { it.id }

                val prompt = """
                    Analyze receipt. Return valid JSON only.
                    Context:
                    - User's Wallets: [$walletListString] (Pick the closest match or Default to first one)
                    - Valid Categories: [$categoryListString] (Pick one ID)
                    
                    JSON Fields:
                    - "type": "INCOME" or "EXPENSE"
                    - "amount": integer (numeric only)
                    - "category": string (must be one of the Valid Categories IDs above)
                    - "wallet_name": string (must be one of User's Wallets above)
                    - "note": string (short description)
                """.trimIndent()

                val request = GroqRequest(
                    messages = listOf(
                        GroqMessage(
                            content = listOf(
                                ContentPart.Text(text = prompt),
                                ContentPart.Image(image_url = ImageUrl("data:image/jpeg;base64,$base64"))
                            )
                        )
                    )
                )

                val response = groqApi.analyzeImage("Bearer $GROQ_API_KEY", request)
                val jsonString = response.choices.first().message.content

                val result = Gson().fromJson(jsonString, ExtractedTransaction::class.java)

                val matchedWallet = _state.value.availableWallets.find {
                    it.name.equals(result.wallet_name, ignoreCase = true)
                } ?: _state.value.availableWallets.firstOrNull()

                _state.update {
                    it.copy(
                        isAnalyzing = false,
                        amount = result.amount.toString(),
                        note = result.note,
                        type = result.type.uppercase(),
                        selectedWalletId = matchedWallet?.id ?: "",
                        selectedCategoryId = result.category
                    )
                }

            } catch (e: Exception) {
                _state.update { it.copy(isAnalyzing = false, error = e.message ?: "Analysis failed") }
            }
        }
    }

    fun saveTransaction() {
        val currentState = _state.value
        if (currentState.selectedWalletId.isEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val user = authRepository.getCurrentUser() ?: return@launch

            val transaction = Transaction(
                userId = user.uid,
                walletId = currentState.selectedWalletId,
                amount = currentState.amount.toLongOrNull() ?: 0L,
                type = currentState.type,
                category = currentState.selectedCategoryId,
                note = currentState.note,
                date = Date()
            )

            transactionRepository.addTransaction(transaction)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, isSaved = true) }
                }
                .onFailure { exception ->
                    _state.update {
                        it.copy(isLoading = false, error = exception.message)
                    }
                }
        }
    }

    private fun encodeImageToBase64(resolver: android.content.ContentResolver, uri: Uri): String? {
        return try {
            val inputStream = resolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
        } catch (e: Exception) {
            null
        }
    }

    fun onAmountChange(v: String) { _state.update { it.copy(amount = v) } }
    fun onNoteChange(v: String) { _state.update { it.copy(note = v) } }
    fun onWalletChange(name: String) {
        val w = _state.value.availableWallets.find { it.name == name }
        if (w != null) _state.update { it.copy(selectedWalletId = w.id) }
    }
    fun onCategoryChange(name: String) {
        val cat = CategoryData.allCategories.find { it.name == name }
        if (cat != null) _state.update { it.copy(selectedCategoryId = cat.id) }
    }
}