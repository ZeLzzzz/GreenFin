package com.example.financeaudit.presentation.util

import java.text.NumberFormat
import java.util.Locale

fun formatRupiah(amount: Long): String {
    val localeID = Locale("id", "ID")
    val format = NumberFormat.getCurrencyInstance(localeID)

    format.maximumFractionDigits = 0

    return format.format(amount)
}

fun parseRupiahInput(input: String): Long {
    val cleanString = input.replace(Regex("\\D"), "")
    return cleanString.toLongOrNull() ?: 0L
}