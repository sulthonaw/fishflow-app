package com.fishflow.app.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import java.net.UnknownHostException

class ErrorMessageMapperTest {

    @Test
    fun mapsRawInvalidCredentialsWithHttpDetailsToFriendlyMessage() {
        val raw = """
            invalid_credentials (Invalid login credentials: invalid_credentials)
            URL: https://udzasxzpltsdudbxymnu.supabase.co/auth/v1/token?grant_type=password
            Headers: [Authorization=[Bearer sb_publishable_xxx], apikey=[sb_publishable_xxx]]
            Http Method: POST
        """.trimIndent()

        val result = ErrorMessageMapper.sanitize(raw, fallback = "Login gagal.")

        assertEquals("Email atau password salah.", result)
        assertFalse(result.contains("URL:"))
        assertFalse(result.contains("Headers:"))
    }

    @Test
    fun mapsUnknownHostExceptionToNetworkMessage() {
        val result = ErrorMessageMapper.from(
            UnknownHostException("udzasxzpltsdudbxymnu.supabase.co"),
            fallback = "Gagal memuat data."
        )

        assertEquals(
            "Koneksi internet bermasalah. Periksa jaringan Anda lalu coba lagi.",
            result
        )
    }

    @Test
    fun hidesUnrecognizedTechnicalMessageBehindFallback() {
        val raw = "PostgrestRestException: column foo does not exist URL: https://example.com"
        val result = ErrorMessageMapper.sanitize(raw, fallback = "Gagal memuat produk.")

        assertEquals("Gagal memuat produk.", result)
    }

    @Test
    fun preservesUserFriendlyValidationMessage() {
        val result = ErrorMessageMapper.sanitize(
            "Harga harus lebih dari Rp0.",
            fallback = "Gagal menyimpan."
        )

        assertEquals("Harga harus lebih dari Rp0.", result)
    }

    @Test
    fun mapsRateLimitToFriendlyMessage() {
        val result = ErrorMessageMapper.sanitize(
            "over_email_send_rate_limit (429 Too Many Requests)",
            fallback = "Pendaftaran gagal."
        )

        assertEquals("Terlalu banyak percobaan. Silakan tunggu beberapa menit lalu coba lagi.", result)
    }
}
