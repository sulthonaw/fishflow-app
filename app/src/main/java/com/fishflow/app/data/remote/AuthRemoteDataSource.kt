
package com.fishflow.app.data.remote

data class MockAuthUser(
    val id: String,
    val email: String,
    val nama: String,
    val role: String
)

class AuthRemoteDataSource {

    private var currentUser: MockAuthUser? = null
    suspend fun login(
        email: String,
        password: String
    ): Result<MockAuthUser> {

        return try {
            if (email.isBlank() || password.isBlank()) {
                return Result.failure(
                    IllegalArgumentException("Email dan password wajib diisi.")
                )
            }

            if (!email.contains("@")) {
                return Result.failure(
                    IllegalArgumentException("Format email tidak valid.")
                )
            }

            if (password.length < 8) {
                return Result.failure(
                    IllegalArgumentException("Password minimal 8 karakter.")
                )
            }

            val mockUser = MockAuthUser(
                id = "mock-user-001",
                email = email,
                nama = email.substringBefore("@"),
                role = "SELLER"
            )

            currentUser = mockUser

            Result.success(mockUser)

        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    suspend fun register(
        email: String,
        password: String,
        nama: String,
        role: String
    ): Result<MockAuthUser> {

        return try {
            if (email.isBlank() || password.isBlank() || nama.isBlank()) {
                return Result.failure(
                    IllegalArgumentException("Semua field wajib diisi.")
                )
            }

            if (!email.contains("@")) {
                return Result.failure(
                    IllegalArgumentException("Format email tidak valid.")
                )
            }

            if (password.length < 8) {
                return Result.failure(
                    IllegalArgumentException("Password minimal 8 karakter.")
                )
            }

            val mockUser = MockAuthUser(
                id = "mock-user-${System.currentTimeMillis()}",
                email = email,
                nama = nama,
                role = role
            )

            currentUser = mockUser

            Result.success(mockUser)

        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            currentUser = null
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    suspend fun getCurrentUser(): MockAuthUser? {
        return currentUser
    }

    suspend fun isLoggedIn(): Boolean {
        return currentUser != null
    }
}
