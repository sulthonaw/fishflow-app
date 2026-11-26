package com.fishflow.app.core.util

object ValidationUtils {
    private const val EPSILON = 0.000001
    private val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val rupiahPattern = Regex("^(\\d+|\\d{1,3}(\\.\\d{3})+)$")

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
            password.length < Constants.MIN_PASSWORD_LENGTH ->
                "Password minimal ${Constants.MIN_PASSWORD_LENGTH} karakter."
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
