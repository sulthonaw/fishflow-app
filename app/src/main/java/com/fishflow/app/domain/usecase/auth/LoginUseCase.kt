package com.fishflow.app.domain.usecase.auth

import com.fishflow.app.data.model.User
import com.fishflow.app.data.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<User> {
        return authRepository.login(
            email = email,
            password = password
        )
    }
}
