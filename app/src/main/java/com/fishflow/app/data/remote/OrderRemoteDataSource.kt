package com.fishflow.app.data.remote

import com.fishflow.app.core.network.SupabaseClientProvider
import com.fishflow.app.data.model.Order
import com.fishflow.app.data.model.OrderItem
import com.fishflow.app.data.model.OrderStatus
import com.fishflow.app.supabase.RealtimeChannels
import com.fishflow.app.supabase.Tables
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order as PostgrestOrder
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Serializable
data class RemoteCartItemDto(
    val id: String = "",
    @SerialName("buyer_id") val buyerId: String = "",
    @SerialName("product_id") val productId: String = "",
    @SerialName("qty_kg") val qtyKg: Double = 0.0
)

@Serializable
private data class UpsertCartItemPayload(
    @SerialName("buyer_id") val buyerId: String,
    @SerialName("product_id") val productId: String,
    @SerialName("qty_kg") val qtyKg: Double
)

@Serializable
private data class OrderItemDto(
    val id: String = "",
    @SerialName("order_id") val orderId: String = "",
    @SerialName("product_id") val productId: String = "",
    @SerialName("nama_ikan") val namaIkan: String = "",
    val qty: Double = 0.0,
    @SerialName("harga_satuan") val hargaSatuan: Double = 0.0,
    val subtotal: Double = 0.0
) {
    fun toDomain(): OrderItem = OrderItem(
        id = id,
        orderId = orderId,
        productId = productId,
        namaIkan = namaIkan,
        qty = qty,
        hargaSatuan = hargaSatuan,
        subtotal = subtotal
    )
}

@Serializable
private data class SimpleProfileDto(
    val nama: String = ""
)

@Serializable
private data class SellerJoinProfileDto(
    val nama: String = "",
    @SerialName("seller_profiles") val sellerProfiles: SellerVesselJoinDto? = null
)

@Serializable
private data class SellerVesselJoinDto(
    @SerialName("nama_kapal") val namaKapal: String = ""
)

@Serializable
private data class OrderDto(
    val id: String = "",
    @SerialName("order_code") val orderCode: String = "",
    @SerialName("buyer_id") val buyerId: String = "",
    @SerialName("seller_id") val sellerId: String = "",
    @SerialName("pickup_point") val pickupPoint: String = "",
    @SerialName("extra_fee") val extraFee: Double = 5000.0,
    @SerialName("total_harga") val totalHarga: Double = 0.0,
    val status: String = OrderStatus.MENUNGGU_KONFIRMASI.name,
    @SerialName("kode_pickup") val kodePickup: String? = null,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
    @SerialName("order_items") val orderItems: List<OrderItemDto> = emptyList(),
    @SerialName("buyer") val buyer: SimpleProfileDto? = null,
    @SerialName("seller") val seller: SellerJoinProfileDto? = null
) {
    fun toDomain(): Order = Order(
        id = id,
        orderCode = orderCode.ifBlank { "ORD-${id.take(6).uppercase()}" },
        buyerId = buyerId,
        buyerName = buyer?.nama?.ifBlank { "Wisatawan" } ?: "Wisatawan",
        buyerSegment = "Wisatawan",
        sellerId = sellerId,
        sellerName = seller?.nama?.ifBlank { "Penjual FishFlow" } ?: "Penjual FishFlow",
        vesselName = seller?.sellerProfiles?.namaKapal.orEmpty(),
        pickupPoint = pickupPoint,
        items = orderItems.map { it.toDomain() },
        extraFee = extraFee,
        totalHarga = totalHarga,
        status = runCatching { OrderStatus.valueOf(status) }
            .getOrDefault(OrderStatus.MENUNGGU_KONFIRMASI),
        kodePickup = kodePickup,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

class OrderRemoteDataSource(
    private val supabase: SupabaseClient = SupabaseClientProvider.client
) {
    private val orderSelectColumns = Columns.raw(
        "*, order_items(*), buyer:profiles!orders_buyer_id_fkey(nama), seller:profiles!orders_seller_id_fkey(nama, seller_profiles(nama_kapal))"
    )

    suspend fun getCartItems(buyerId: String): List<RemoteCartItemDto> {
        return supabase.postgrest[Tables.CART_ITEMS]
            .select {
                filter { eq("buyer_id", buyerId) }
            }
            .decodeList<RemoteCartItemDto>()
    }

    suspend fun upsertCartItem(buyerId: String, productId: String, qtyKg: Double) {
        supabase.postgrest[Tables.CART_ITEMS]
            .upsert(
                UpsertCartItemPayload(
                    buyerId = buyerId,
                    productId = productId,
                    qtyKg = qtyKg
                )
            ) {
                onConflict = "buyer_id,product_id"
            }
    }

    suspend fun removeCartItem(buyerId: String, productId: String) {
        supabase.postgrest[Tables.CART_ITEMS]
            .delete {
                filter {
                    eq("buyer_id", buyerId)
                    eq("product_id", productId)
                }
            }
    }

    suspend fun clearCart(buyerId: String) {
        supabase.postgrest[Tables.CART_ITEMS]
            .delete {
                filter { eq("buyer_id", buyerId) }
            }
    }

    suspend fun createOrderAtomic(
        buyerId: String,
        sellerId: String,
        pickupPoint: String,
        extraFee: Double,
        items: List<OrderItem>
    ): String {
        val itemsJson = buildJsonArray {
            items.forEach { item ->
                add(
                    buildJsonObject {
                        put("product_id", item.productId)
                        put("qty", item.qty)
                    }
                )
            }
        }
        val params = buildJsonObject {
            put("p_seller_id", sellerId)
            put("p_pickup_point", pickupPoint)
            put("p_extra_fee", extraFee)
            put("p_items", itemsJson)
        }
        val orderId = supabase.postgrest
            .rpc("create_order_atomic", params)
            .decodeAs<String>()

        runCatching { clearCart(buyerId) }
        return orderId
    }

    suspend fun confirmOrderAtomic(orderId: String) {
        val params = buildJsonObject { put("p_order_id", orderId) }
        supabase.postgrest.rpc("confirm_order_atomic", params)
    }

    suspend fun rejectOrderAtomic(orderId: String) {
        val params = buildJsonObject { put("p_order_id", orderId) }
        supabase.postgrest.rpc("reject_order_atomic", params)
    }

    suspend fun completeOrderAtomic(orderId: String, kodePickup: String) {
        val params = buildJsonObject {
            put("p_order_id", orderId)
            put("p_kode_pickup", kodePickup.trim())
        }
        supabase.postgrest.rpc("complete_order_atomic", params)
    }

    suspend fun cancelOrderAtomic(orderId: String) {
        val params = buildJsonObject { put("p_order_id", orderId) }
        supabase.postgrest.rpc("cancel_order_atomic", params)
    }

    suspend fun getOrdersByBuyer(buyerId: String): List<Order> {
        return supabase.postgrest[Tables.ORDERS]
            .select(columns = orderSelectColumns) {
                filter { eq("buyer_id", buyerId) }
                order("created_at", PostgrestOrder.DESCENDING)
            }
            .decodeList<OrderDto>()
            .map { it.toDomain() }
    }

    suspend fun getOrdersBySeller(sellerId: String): List<Order> {
        return supabase.postgrest[Tables.ORDERS]
            .select(columns = orderSelectColumns) {
                filter { eq("seller_id", sellerId) }
                order("created_at", PostgrestOrder.DESCENDING)
            }
            .decodeList<OrderDto>()
            .map { it.toDomain() }
    }

    suspend fun getOrderById(orderId: String): Order? {
        return supabase.postgrest[Tables.ORDERS]
            .select(columns = orderSelectColumns) {
                filter { eq("id", orderId) }
            }
            .decodeSingleOrNull<OrderDto>()
            ?.toDomain()
    }

    fun observeOrder(orderId: String): Flow<Order?> = flow {
        emit(getOrderById(orderId))
        val channel = supabase.channel("${RealtimeChannels.ORDERS_CHANNEL}-$orderId")
        val changes = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = Tables.ORDERS
        }
        channel.subscribe()
        try {
            emitAll(changes.map { getOrderById(orderId) })
        } finally {
            runCatching { channel.unsubscribe() }
        }
    }

    fun observeOrdersBySeller(sellerId: String): Flow<List<Order>> = flow {
        emit(getOrdersBySeller(sellerId))
        val channel = supabase.channel(RealtimeChannels.ordersForUser(sellerId))
        val changes = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = Tables.ORDERS
        }
        channel.subscribe()
        try {
            emitAll(changes.map { getOrdersBySeller(sellerId) })
        } finally {
            runCatching { channel.unsubscribe() }
        }
    }

    fun observeOrdersByBuyer(buyerId: String): Flow<List<Order>> = flow {
        emit(getOrdersByBuyer(buyerId))
        val channel = supabase.channel(RealtimeChannels.ordersForUser(buyerId))
        val changes = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = Tables.ORDERS
        }
        channel.subscribe()
        try {
            emitAll(changes.map { getOrdersByBuyer(buyerId) })
        } finally {
            runCatching { channel.unsubscribe() }
        }
    }
}
