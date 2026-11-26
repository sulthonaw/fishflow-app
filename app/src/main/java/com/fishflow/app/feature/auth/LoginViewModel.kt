package com.fishflow.app.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishflow.app.core.util.ValidationUtils
import com.fishflow.app.data.model.UserRole
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            errorMessage = null
        )
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            errorMessage = null
        )
    }

    fun fillDemoAccount(role: UserRole) {
        val demoEmail = when (role) {
            UserRole.WISATAWAN -> "wisatawan@fishflow.com"
            UserRole.PENJUAL -> "seller@fishflow.com"
            UserRole.ADMIN -> "admin@fishflow.com"
        }
        _uiState.value = _uiState.value.copy(
            email = demoEmail,
            password = "password123",
            errorMessage = null
        )
    }

    fun onLoginClick() {
        val currentState = _uiState.value
        val emailError = ValidationUtils.validateEmail(currentState.email)
        if (emailError != null) {
            _uiState.value = currentState.copy(errorMessage = emailError)
            return
        }

        val passwordError = ValidationUtils.validatePassword(currentState.password)
        if (passwordError != null) {
            _uiState.value = currentState.copy(errorMessage = passwordError)
            return
        }

        if (currentState.isLoading) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                isLoginSuccess = false
            )

            delay(350)
            val matched = InMemoryAuthStore.authenticate(
                email = currentState.email,
                password = currentState.password
            )

            if (matched != null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoginSuccess = true,
                    authenticatedRole = matched.role,
                    authenticatedName = matched.name,
                    errorMessage = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoginSuccess = false,
                    errorMessage = "Email atau password belum terdaftar. Gunakan akun demo atau daftar akun baru."
                )
            }
        }
    }

    fun resetLoginSuccess() {
        _uiState.value = _uiState.value.copy(isLoginSuccess = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
