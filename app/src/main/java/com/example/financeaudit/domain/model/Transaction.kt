package com.example.financeaudit.domain.model

import java.util.Date

data class Transaction(
    val id: String = "",
    val userId: String = "",
    val walletId: String = "",
    val amount: Long = 0,
    val type: String = "EXPENSE",
    val category: String = "General",
    val date: Date = Date(),
    val note: String = ""
)

