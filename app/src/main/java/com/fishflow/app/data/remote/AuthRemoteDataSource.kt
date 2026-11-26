package com.fishflow.app.data.remote

import com.fishflow.app.core.network.SupabaseClientProvider
import com.fishflow.app.data.model.User
import com.fishflow.app.data.model.UserRole
import com.fishflow.app.supabase.Tables
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Serializable
data class ProfileDto(
    val id: String,
    val email: String,
    val nama: String,
    val role: String = "WISATAWAN",
    @SerialName("foto_url") val fotoUrl: String? = null,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String = ""
) {
    fun toDomain(): User = User(
        id = id,
        email = email,
        nama = nama,
        role = runCatching { UserRole.valueOf(role) }.getOrDefault(UserRole.WISATAWAN),
        fotoUrl = fotoUrl,
        createdAt = createdAt
    )
}

class AuthRemoteDataSource(
    private val supabase: SupabaseClient = SupabaseClientProvider.client
) {

    suspend fun login(
        email: String,
        password: String
    ): Result<User> = runCatching {
        val normalizedEmail = email.trim().lowercase()
        require(normalizedEmail.isNotBlank() && password.isNotBlank()) {
            "Email dan password wajib diisi."
        }
        require(normalizedEmail.contains("@")) {
            "Format email tidak valid."
        }
        require(password.length >= 8) {
            "Password minimal 8 karakter."
        }

        try {
            supabase.auth.signInWith(Email) {
                this.email = normalizedEmail
                this.password = password
            }
        } catch (e: Exception) {
            val msg = e.message.orEmpty()
            when {
                msg.contains("invalid_credentials", ignoreCase = true) ||
                    msg.contains("Invalid login credentials", ignoreCase = true) ->
                    error("Email atau password salah.")
                msg.contains("Email not confirmed", ignoreCase = true) ->
                    error("Email belum dikonfirmasi. Silakan cek email Anda.")
                else -> throw e
            }
        }

        val authUser = supabase.auth.currentUserOrNull()
            ?: error("Gagal memuat sesi pengguna setelah login.")

        val profile = fetchProfile(authUser.id)
            ?: ProfileDto(
                id = authUser.id,
                email = authUser.email ?: normalizedEmail,
                nama = normalizedEmail.substringBefore("@"),
                role = UserRole.WISATAWAN.name
            )

        if (!profile.isActive) {
            runCatching { supabase.auth.signOut() }
            error("Akun Anda dinonaktifkan oleh admin.")
        }

        profile.toDomain()
    }

    suspend fun register(
        nama: String,
        email: String,
        password: String,
        role: UserRole
    ): Result<User> = runCatching {
        val normalizedEmail = email.trim().lowercase()
        val trimmedName = nama.trim()
        require(trimmedName.isNotBlank() && normalizedEmail.isNotBlank() && password.isNotBlank()) {
            "Semua field wajib diisi."
        }
        require(normalizedEmail.contains("@")) {
            "Format email tidak valid."
        }
        require(password.length >= 8) {
            "Password minimal 8 karakter."
        }

        val signedUp = supabase.auth.signUpWith(Email) {
            this.email = normalizedEmail
            this.password = password
            data = buildJsonObject {
                put("nama", trimmedName)
                put("role", role.name)
            }
        }

        val userId = supabase.auth.currentUserOrNull()?.id ?: signedUp?.id ?: ""
        if (userId.isNotBlank()) {
            fetchProfile(userId)?.toDomain() ?: User(
                id = userId,
                email = normalizedEmail,
                nama = trimmedName,
                role = role,
                fotoUrl = null,
                createdAt = ""
            )
        } else {
            User(
                id = "",
                email = normalizedEmail,
                nama = trimmedName,
                role = role,
                fotoUrl = null,
                createdAt = ""
            )
        }
    }

    suspend fun logout(): Result<Unit> = runCatching {
        supabase.auth.signOut()
    }

    suspend fun getCurrentUser(): User? {
        val authUser = supabase.auth.currentUserOrNull() ?: return null
        val profile = runCatching { fetchProfile(authUser.id) }.getOrNull()
        if (profile != null && !profile.isActive) {
            runCatching { supabase.auth.signOut() }
            return null
        }
        return profile?.toDomain() ?: User(
            id = authUser.id,
            email = authUser.email.orEmpty(),
            nama = authUser.email?.substringBefore("@").orEmpty(),
            role = UserRole.WISATAWAN,
            fotoUrl = null,
            createdAt = ""
        )
    }

    fun isLoggedIn(): Boolean = supabase.auth.currentUserOrNull() != null

    fun observeAuthState(): Flow<User?> =
        supabase.auth.sessionStatus.map { status ->
            when (status) {
                is SessionStatus.Authenticated -> getCurrentUser()
                else -> null
            }
        }

    private suspend fun fetchProfile(userId: String): ProfileDto? {
        return supabase.postgrest[Tables.PROFILES]
            .select {
                filter {
                    eq("id", userId)
                }
            }
            .decodeSingleOrNull<ProfileDto>()
    }
}
