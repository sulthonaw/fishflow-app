package com.fishflow.app.supabase

import com.fishflow.app.BuildConfig

object SupabaseConfig {
    val SUPABASE_URL: String = BuildConfig.SUPABASE_URL
    val SUPABASE_PUBLISHABLE_KEY: String = BuildConfig.SUPABASE_PUBLISHABLE_KEY
    const val STORAGE_BUCKET: String = "fishflow-media"
}
