package com.fishflow.app.data.repository

import com.fishflow.app.data.model.User
import com.fishflow.app.data.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepositoryImpl : AuthRepository {

    private val authState = MutableStateFlow<User?>(null)

    private val mockUsers = mutableListOf(
        User(
            id = "seller-001",
            email = "seller@fishflow.com",
            nama = "Nelayan Tambakrejo",
            role = UserRole.PENJUAL,
            fotoUrl = null,
            createdAt = "2026-11-25"
        ),
        User(
            id = "buyer-001",
            email = "buyer@fishflow.com",
            nama = "Wisatawan FishFlow",
            role = UserRole.WISATAWAN,
            fotoUrl = null,
            createdAt = "2026-11-25"
        )
    )

    private val mockPasswords = mutableMapOf(
        "seller@fishflow.com" to "password123",
        "buyer@fishflow.com" to "password123"
    )

    override suspend fun login(
        email: String,
        password: String
    ): Result<User> {
        val normalizedEmail = email.trim().lowercase()

        val user = mockUsers.find {
            it.email.lowercase() == normalizedEmail
        }

        if (user == null) {
            return Result.failure(
                Exception("Email belum terdaftar.")
            )
        }

        val correctPassword = mockPasswords[normalizedEmail]

        if (correctPassword != password) {
            return Result.failure(
                Exception("Password salah.")
            )
        }

        authState.value = user

        return Result.success(user)
    }

    override suspend fun register(
        nama: String,
        email: String,
        password: String,
        role: UserRole
    ): Result<User> {
        val normalizedEmail = email.trim().lowercase()

        if (mockUsers.any {
                it.email.lowercase() == normalizedEmail
            }) {
            return Result.failure(
                Exception("Email sudah terdaftar.")
            )
        }

        val newUser = User(
            id = "user-${mockUsers.size + 1}",
            email = normalizedEmail,
            nama = nama.trim(),
            role = role,
            fotoUrl = null,
            createdAt = "2026-11-25"
        )

        mockUsers.add(newUser)
        mockPasswords[normalizedEmail] = password

        authState.value = newUser

        return Result.success(newUser)
    }

    override suspend fun logout(): Result<Unit> {
        authState.value = null
        return Result.success(Unit)
    }

    override suspend fun getCurrentUser(): User? {
        return authState.value
    }

    override fun observeAuthState(): Flow<User?> {
        return authState.asStateFlow()
    }

    override suspend fun isLoggedIn(): Boolean {
        return authState.value != null
    }
}
