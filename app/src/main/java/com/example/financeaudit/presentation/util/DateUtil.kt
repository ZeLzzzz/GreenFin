package com.example.financeaudit.presentation.util

import com.example.financeaudit.domain.model.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDateHeader(date: Date): String {
    val formatter = SimpleDateFormat("EEEE, d MMM yyyy", Locale.getDefault())
    return formatter.format(date)
}

fun groupTransactionsByDate(transactions: List<Transaction>): Map<String, List<Transaction>> {
    return transactions
        .groupBy { formatDateHeader(it.date) }
}