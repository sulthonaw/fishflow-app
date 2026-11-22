package com.fishflow.app.core.util

object ValidationUtils {
    private const val EPSILON = 0.000001
    private val emailPattern = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val rupiahPattern = Regex("^(\\d+|\\d{1,3}(\\.\\d{3})+)$")

    fun validateEmail(email: String): String? {
        val value = email.trim()
        if (value.isEmpty()) return "Email wajib diisi."
        if (!emailPattern.matches(value)) return "Format email tidak valid."
        return null
    }

    fun validatePassword(password: String): String? {
        if (password.isEmpty()) return "Password wajib diisi."
        if (password.length < Constants.MIN_PASSWORD_LENGTH) {
            return "Password minimal ${Constants.MIN_PASSWORD_LENGTH} karakter."
        }
        return null
    }

    fun validateWeight(weightText: String): String? {
        val value = weightText.trim().replace(',', '.')
        if (value.isEmpty()) return "Berat tangkapan wajib diisi."

        val weight = value.toDoubleOrNull()
            ?: return "Berat tangkapan harus berupa angka."

        if (!weight.isFinite()) return "Berat tangkapan tidak valid."
        if (weight <= 0.0) return "Berat tangkapan harus lebih dari 0 kg."
        return null
    }

    fun validateWeight(weightKg: Double): String? {
        if (!weightKg.isFinite()) return "Berat tangkapan tidak valid."
        if (weightKg <= 0.0) return "Berat tangkapan harus lebih dari 0 kg."
        return null
    }

    /**
     * Harga menerima angka biasa (45000) atau pemisah ribuan titik (45.000).
     * Desimal tidak digunakan untuk harga Rupiah pada rancangan awal aplikasi.
     */
    fun validatePrice(priceText: String): String? {
        val value = priceText.trim()
        if (value.isEmpty()) return "Harga ikan wajib diisi."
        if (!rupiahPattern.matches(value)) {
            return "Masukkan harga dalam angka, contoh: 45000 atau 45.000."
        }

        val price = value.replace(".", "").toLongOrNull()
            ?: return "Harga ikan terlalu besar atau tidak valid."

        if (price <= 0L) return "Harga ikan harus lebih dari Rp 0."
        return null
    }

    fun validatePrice(price: Long): String? {
        if (price <= 0L) return "Harga ikan harus lebih dari Rp 0."
        return null
    }

    fun validateQuantity(quantityText: String, availableStockKg: Double): String? {
        val value = quantityText.trim().replace(',', '.')
        if (value.isEmpty()) return "Jumlah pesanan wajib diisi."

        val quantity = value.toDoubleOrNull()
            ?: return "Jumlah pesanan harus berupa angka."

        return validateQuantity(quantity, availableStockKg)
    }

    fun validateQuantity(quantityKg: Double, availableStockKg: Double): String? {
        if (!availableStockKg.isFinite() || availableStockKg < 0.0) {
            return "Data stok tersedia tidak valid."
        }
        if (!quantityKg.isFinite()) return "Jumlah pesanan tidak valid."
        if (quantityKg <= 0.0) return "Jumlah pesanan harus lebih dari 0 kg."
        if (quantityKg - availableStockKg > EPSILON) {
            return "Jumlah pesanan melebihi stok yang tersedia."
        }
        return null
    }
}
