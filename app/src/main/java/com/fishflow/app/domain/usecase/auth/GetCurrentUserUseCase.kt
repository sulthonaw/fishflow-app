package com.fishflow.app.domain.usecase.auth

import com.fishflow.app.data.model.User
import com.fishflow.app.data.repository.AuthRepository

class GetCurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): User? {
        return authRepository.getCurrentUser()
    }
}
