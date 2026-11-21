package com.fishflow.app.data.model

data class User(
    val id: String,
    val email: String,
    val nama: String,
    val role: UserRole,
    val fotoUrl: String? = null,
    val createdAt: String
)
