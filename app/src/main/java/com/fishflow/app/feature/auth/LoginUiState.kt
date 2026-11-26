package com.fishflow.app.feature.auth

import com.fishflow.app.data.model.UserRole

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccess: Boolean = false,
    val authenticatedRole: UserRole? = null,
    val authenticatedName: String = ""
)
