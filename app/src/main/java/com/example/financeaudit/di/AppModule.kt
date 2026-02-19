package com.example.financeaudit.di

import com.example.financeaudit.data.remote.GroqApi
import android.content.Context
import com.example.financeaudit.data.local.SessionManager
import com.example.financeaudit.data.repository.AuthRepositoryImpl
import com.example.financeaudit.data.repository.TransactionRepositoryImpl
import com.example.financeaudit.data.repository.UserRepositoryImpl
import com.example.financeaudit.data.repository.WalletRepositoryImpl
import com.example.financeaudit.domain.repository.AuthRepository
import com.example.financeaudit.domain.repository.TransactionRepository
import com.example.financeaudit.domain.repository.UserRepository
import com.example.financeaudit.domain.repository.WalletRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore{
        return Firebase.firestore
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        @ApplicationContext context: Context
    ): AuthRepository {
        return AuthRepositoryImpl(auth, context)
    }

    @Provides
    @Singleton
    fun provideUserRepository(firestore: FirebaseFirestore): UserRepository {
        return UserRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideWalletRepository(firestore: FirebaseFirestore): WalletRepository {
        return WalletRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideTransactionRepository(firestore: FirebaseFirestore): TransactionRepository {
        return TransactionRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideGroqApi(): GroqApi {
        return Retrofit.Builder()
            .baseUrl("https://api.groq.com/openai/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GroqApi::class.java)
    }
}
