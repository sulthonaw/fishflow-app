package com.fishflow.app.supabase

object RealtimeChannels {
    const val PRODUCTS_CHANNEL = "products-realtime"
    const val ORDERS_CHANNEL = "orders-realtime"

    fun ordersForUser(userId: String): String = "orders:$userId"
}
