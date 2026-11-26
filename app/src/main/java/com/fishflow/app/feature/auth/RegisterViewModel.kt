
package com.fishflow.app.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishflow.app.core.util.ValidationUtils
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(
            name = name,
            errorMessage = null
        )
    }

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

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            errorMessage = null
        )
    }

    fun onRoleChange(role: String) {
        _uiState.value = _uiState.value.copy(
            selectedRole = role,
            errorMessage = null
        )
    }

    fun onRegisterClick() {
        val currentState = _uiState.value

        if (currentState.isLoading) return

        val nameError = ValidationUtils.validateName(currentState.name)
        if (nameError != null) {
            _uiState.value = currentState.copy(
                errorMessage = nameError
            )
            return
        }

        val emailError = ValidationUtils.validateEmail(currentState.email)
        if (emailError != null) {
            _uiState.value = currentState.copy(
                errorMessage = emailError
            )
            return
        }

        val passwordError = ValidationUtils.validatePassword(currentState.password)
        if (passwordError != null) {
            _uiState.value = currentState.copy(
                errorMessage = passwordError
            )
            return
        }

        val confirmPasswordError = ValidationUtils.validateConfirmPassword(
            currentState.password,
            currentState.confirmPassword
        )

        if (confirmPasswordError != null) {
            _uiState.value = currentState.copy(
                errorMessage = confirmPasswordError
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                isRegisterSuccess = false
            )

            try {
                delay(1000)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRegisterSuccess = true,
                    errorMessage = null
                )

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRegisterSuccess = false,
                    errorMessage = e.message
                        ?: "Terjadi kesalahan saat registrasi."
                )
            }
        }
    }

    fun resetRegisterSuccess() {
        _uiState.value = _uiState.value.copy(
            isRegisterSuccess = false
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
}
