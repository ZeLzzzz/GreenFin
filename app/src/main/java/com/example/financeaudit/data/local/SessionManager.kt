package com.example.financeaudit.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_LOGIN_TIMESTAMP = "login_timestamp"
        private const val SESSION_TIMEOUT = 30 * 60 * 1000L
    }

    fun saveLoginTime() {
        val currentTime = System.currentTimeMillis()
        prefs.edit { putLong(KEY_LOGIN_TIMESTAMP, currentTime) }
    }

    fun isSessionExpired(): Boolean {
        val loginTime = prefs.getLong(KEY_LOGIN_TIMESTAMP, 0)
        val currentTime = System.currentTimeMillis()

        return loginTime == 0L || (currentTime - loginTime) > SESSION_TIMEOUT
    }

    fun clearSession() {
        prefs.edit { clear() }
    }
}