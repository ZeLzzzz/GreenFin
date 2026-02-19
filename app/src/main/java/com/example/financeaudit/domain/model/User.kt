package com.example.financeaudit.domain.model

data class User (
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val lastLogin: Long = System.currentTimeMillis(),
)
