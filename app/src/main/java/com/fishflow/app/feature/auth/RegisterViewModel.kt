package com.fishflow.app.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishflow.app.core.util.ValidationUtils
import com.fishflow.app.data.model.UserRole
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name, errorMessage = null)
    }

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword, errorMessage = null)
    }

    fun onRoleChange(role: UserRole) {
        _uiState.value = _uiState.value.copy(selectedRole = role, errorMessage = null)
    }

    fun onRegisterClick() {
        val currentState = _uiState.value
        if (currentState.isLoading) return

        val nameError = ValidationUtils.validateName(currentState.name)
        if (nameError != null) {
            _uiState.value = currentState.copy(errorMessage = nameError)
            return
        }

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

        val confirmPasswordError = ValidationUtils.validateConfirmPassword(
            currentState.password,
            currentState.confirmPassword
        )
        if (confirmPasswordError != null) {
            _uiState.value = currentState.copy(errorMessage = confirmPasswordError)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                isRegisterSuccess = false
            )

            delay(350)
            InMemoryAuthStore.register(
                name = currentState.name,
                email = currentState.email,
                password = currentState.password,
                role = currentState.selectedRole
            )

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isRegisterSuccess = true,
                errorMessage = null
            )
        }
    }

    fun resetRegisterSuccess() {
        _uiState.value = _uiState.value.copy(isRegisterSuccess = false)
    }
}
