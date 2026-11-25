
package com.fishflow.app.feature.auth

object ValidationUtils {
    fun validateName(name: String): String? {
        return if (name.trim().isEmpty()) {
            "Nama tidak boleh kosong."
        } else {
            null
        }
    }

    fun validateEmail(email: String): String? {
        val trimmedEmail = email.trim()

        if (trimmedEmail.isEmpty()) {
            return "Email tidak boleh kosong."
        }

        val emailPattern =
            Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

        return if (!emailPattern.matches(trimmedEmail)) {
            "Format email tidak valid."
        } else {
            null
        }
    }

    /**
     * Password minimal 8 karakter.
     */
    fun validatePassword(password: String): String? {
        return when {
            password.isEmpty() ->
                "Password tidak boleh kosong."

            password.length < 8 ->
                "Password minimal 8 karakter."

            else ->
                null
        }
    }

    fun validateConfirmPassword(
        password: String,
        confirmPassword: String
    ): String? {
        return when {
            confirmPassword.isEmpty() ->
                "Konfirmasi password tidak boleh kosong."

            password != confirmPassword ->
                "Password dan konfirmasi password tidak sama."

            else ->
                null
        }
    }
}
