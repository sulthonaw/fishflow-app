package com.fishflow.app.core.util

object Constants {
    const val APP_NAME = "FISH FLOW"
    const val CURRENCY_CODE = "IDR"
    const val WEIGHT_UNIT_KG = "kg"
    const val MIN_PASSWORD_LENGTH = 6

    object StorageBuckets {
        const val CATCH_PHOTOS = "catch-photos"
        const val PRODUCT_IMAGES = "product-images"
    }

    /** Key untuk Preferences DataStore. */
    object DataStoreKeys {
        const val USER_ID = "user_id"
        const val USER_NAME = "user_name"
        const val USER_ROLE = "user_role"
        const val IS_LOGGED_IN = "is_logged_in"
    }

    /** Pola tampilan tanggal yang digunakan pada form tangkapan dan dashboard. */
    object DatePatterns {
        const val DATE = "dd MMM yyyy"
        const val DATE_TIME = "dd MMM yyyy, HH:mm"
    }
}
