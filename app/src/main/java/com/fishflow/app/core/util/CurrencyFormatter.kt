package com.fishflow.app.core.util

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import java.math.RoundingMode

object CurrencyFormatter {
    private val indonesiaLocale = Locale.forLanguageTag("id-ID")

    fun formatRupiah(amount: Long): String {
        return normalize(createFormatter().format(amount))
    }

    fun formatRupiah(amount: Int): String {
        return formatRupiah(amount.toLong())
    }

    fun formatRupiah(amount: Double): String {
        if (!amount.isFinite()) return "-"
        return normalize(createFormatter().format(amount))
    }

    private fun createFormatter(): NumberFormat {
        return NumberFormat.getCurrencyInstance(indonesiaLocale).apply {
            currency = Currency.getInstance(Constants.CURRENCY_CODE)
            minimumFractionDigits = 0
            maximumFractionDigits = 0
            roundingMode = RoundingMode.HALF_UP
        }
    }

    // Normalisasi agar hasil konsisten sebagai misalnya Rp 680.000.
    private fun normalize(value: String): String {
        val cleaned = value
            .replace('\u00A0', ' ')
            .replace(Regex("\\s+"), " ")
            .trim()

        return cleaned.replace(Regex("^(-?)Rp(?=\\d)"), "$1Rp ")
    }
}
