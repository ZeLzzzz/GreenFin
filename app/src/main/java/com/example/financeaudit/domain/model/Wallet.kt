package com.example.financeaudit.domain.model

data class Wallet(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val type: String = "BANK",
    val balance: Long = 0,
    val createdAt: Long = System.currentTimeMillis()
)