package com.fishflow.app.data.repository

import com.fishflow.app.data.model.User
import com.fishflow.app.data.model.UserRole
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): Result<User>

    suspend fun register(
        nama: String,
        email: String,
        password: String,
        role: UserRole
    ): Result<User>

    suspend fun logout(): Result<Unit>

    suspend fun getCurrentUser(): User?

    suspend fun isLoggedIn(): Boolean

    fun observeAuthState(): Flow<User?>
}
