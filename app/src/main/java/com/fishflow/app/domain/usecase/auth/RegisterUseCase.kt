package com.fishflow.app.domain.usecase.auth

import com.fishflow.app.data.model.User
import com.fishflow.app.data.model.UserRole
import com.fishflow.app.data.repository.AuthRepository

class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        nama: String,
        role: UserRole
    ): Result<User> {
        return authRepository.register(
            email = email,
            password = password,
            nama = nama,
            role = role
        )
    }
}
