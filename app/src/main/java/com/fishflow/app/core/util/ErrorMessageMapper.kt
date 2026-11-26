package com.fishflow.app.core.util

import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorMessageMapper {

    fun from(
        throwable: Throwable?,
        fallback: String = "Terjadi kesalahan. Silakan coba lagi."
    ): String {
        if (throwable == null) return fallback

        if (throwable is UnknownHostException ||
            throwable is ConnectException ||
            throwable is SocketTimeoutException
        ) {
            return "Koneksi internet bermasalah. Periksa jaringan Anda lalu coba lagi."
        }

        return sanitize(throwable.message, fallback)
    }

    fun sanitize(
        rawMessage: String?,
        fallback: String = "Terjadi kesalahan. Silakan coba lagi."
    ): String {
        val msg = rawMessage?.trim().orEmpty()
        if (msg.isBlank()) return fallback

        return when {
            msg.contains("invalid_credentials", ignoreCase = true) ||
                msg.contains("Invalid login credentials", ignoreCase = true) ->
                "Email atau password salah."

            msg.contains("Email not confirmed", ignoreCase = true) ->
                "Email belum dikonfirmasi. Silakan cek kotak masuk Anda."

            msg.contains("User already registered", ignoreCase = true) ||
                msg.contains("already registered", ignoreCase = true) ||
                msg.contains("already exists", ignoreCase = true) ->
                "Email sudah terdaftar. Silakan gunakan akun lain atau login."

            msg.contains("weak_password", ignoreCase = true) ||
                msg.contains("Password should be", ignoreCase = true) ->
                "Password minimal 8 karakter dan cukup kuat."

            msg.contains("Unable to resolve host", ignoreCase = true) ||
                msg.contains("Failed to connect", ignoreCase = true) ||
                msg.contains("Network is unreachable", ignoreCase = true) ||
                msg.contains("timeout", ignoreCase = true) ||
                msg.contains("SocketTimeoutException", ignoreCase = true) ||
                msg.contains("UnknownHostException", ignoreCase = true) ||
                msg.contains("ConnectException", ignoreCase = true) ->
                "Koneksi internet bermasalah. Periksa jaringan Anda lalu coba lagi."

            msg.contains("JWT expired", ignoreCase = true) ||
                msg.contains("token is expired", ignoreCase = true) ->
                "Sesi Anda telah berakhir. Silakan login kembali."

            msg.contains("permission denied", ignoreCase = true) ||
                msg.contains("row-level security", ignoreCase = true) ||
                msg.contains("401 Unauthorized", ignoreCase = true) ||
                msg.contains("403 Forbidden", ignoreCase = true) ->
                "Anda tidak memiliki izin untuk melakukan tindakan ini."

            msg.contains("duplicate key", ignoreCase = true) ||
                msg.contains("23505", ignoreCase = true) ->
                "Data sudah ada dan tidak boleh duplikat."

            msg.contains("foreign key", ignoreCase = true) ||
                msg.contains("23503", ignoreCase = true) ->
                "Data tidak dapat diubah karena masih terhubung dengan data lain."

            msg.contains("Kode pickup tidak valid", ignoreCase = true) ||
                msg.contains("Kode pickup tidak cocok", ignoreCase = true) ->
                "Kode pickup tidak cocok. Periksa kembali kode dari pembeli."

            msg.contains("Stok tidak mencukupi", ignoreCase = true) ||
                msg.contains("Stok produk", ignoreCase = true) ->
                msg.lineSequence().firstOrNull()?.take(120) ?: "Stok ikan tidak mencukupi."

            looksTechnical(msg) -> fallback

            else -> msg
        }
    }

    private fun looksTechnical(message: String): Boolean {
        val technicalMarkers = listOf(
            "URL:",
            "Headers:",
            "Http Method:",
            "https://",
            "http://",
            "Exception",
            "Postgrest",
            "RestException",
            "Ktor",
            "SELECT ",
            "INSERT ",
            "UPDATE ",
            "DELETE ",
            "SQL",
            "{"
        )
        return technicalMarkers.any { marker ->
            message.contains(marker, ignoreCase = true)
        }
    }
}
