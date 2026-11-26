package com.fishflow.app.data.repository

import com.fishflow.app.data.model.User
import com.fishflow.app.data.model.UserRole
import com.fishflow.app.data.remote.AuthRemoteDataSource
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource = AuthRemoteDataSource()
) : AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): Result<User> = remoteDataSource.login(email = email, password = password)

    override suspend fun register(
        nama: String,
        email: String,
        password: String,
        role: UserRole
    ): Result<User> = remoteDataSource.register(
        nama = nama,
        email = email,
        password = password,
        role = role
    )

    override suspend fun logout(): Result<Unit> = remoteDataSource.logout()

    override suspend fun getCurrentUser(): User? = remoteDataSource.getCurrentUser()

    override suspend fun isLoggedIn(): Boolean = remoteDataSource.isLoggedIn()

    override fun observeAuthState(): Flow<User?> = remoteDataSource.observeAuthState()
}
