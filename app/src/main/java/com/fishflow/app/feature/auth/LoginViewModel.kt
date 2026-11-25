
package com.fishflow.app.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CancellationException

class LoginViewModel : ViewModel() {

    private val _uiState =
        kotlinx.coroutines.flow.MutableStateFlow(LoginUiState())

    val uiState =
        _uiState.asStateFlow()

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

    fun onLoginClick() {
        val currentState = _uiState.value

        val emailError =
            ValidationUtils.validateEmail(currentState.email)

        if (emailError != null) {
            _uiState.value = currentState.copy(
                errorMessage = emailError
            )
            return
        }

        val passwordError =
            ValidationUtils.validatePassword(currentState.password)

        if (passwordError != null) {
            _uiState.value = currentState.copy(
                errorMessage = passwordError
            )
            return
        }

        if (currentState.isLoading) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                isLoginSuccess = false
            )

            try {
                delay(1000)

                val email = currentState.email.trim()
                val password = currentState.password

                val isMockLogin =
                    email == "seller@fishflow.com" &&
                    password == "password123"

                if (isMockLogin) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoginSuccess = true,
                        errorMessage = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoginSuccess = false,
                        errorMessage = "Email atau password salah."
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoginSuccess = false,
                    errorMessage = e.message
                        ?: "Terjadi kesalahan saat login."
                )
            }
        }
    }

    fun resetLoginSuccess() {
        _uiState.value = _uiState.value.copy(
            isLoginSuccess = false
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
}
