package com.fishflow.app.feature.auth

import com.fishflow.app.data.model.UserRole

data class LocalAccount(
    val name: String,
    val email: String,
    val password: String,
    val role: UserRole
)

object InMemoryAuthStore {
    private val accounts = mutableListOf(
        LocalAccount(
            name = "Bima Arya",
            email = "wisatawan@fishflow.com",
            password = "password123",
            role = UserRole.WISATAWAN
        ),
        LocalAccount(
            name = "Pak Joko Widodo",
            email = "seller@fishflow.com",
            password = "password123",
            role = UserRole.PENJUAL
        ),
        LocalAccount(
            name = "Admin Pesisir",
            email = "admin@fishflow.com",
            password = "password123",
            role = UserRole.ADMIN
        )
    )

    fun authenticate(email: String, password: String): LocalAccount? {
        val normalizedEmail = email.trim().lowercase()
        return accounts.firstOrNull {
            it.email.lowercase() == normalizedEmail && it.password == password
        }
    }

    fun register(name: String, email: String, password: String, role: UserRole): LocalAccount {
        val normalizedEmail = email.trim().lowercase()
        accounts.removeAll { it.email.lowercase() == normalizedEmail }
        val account = LocalAccount(
            name = name.trim(),
            email = email.trim(),
            password = password,
            role = role
        )
        accounts.add(account)
        return account
    }
}
