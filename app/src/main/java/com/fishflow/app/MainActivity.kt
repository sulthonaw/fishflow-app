// MainActivity.kt
// Developer: Sulthon
//
// Koneksi: Sulthon yang bikin NavGraph, jadi dia juga yang setup Activity
// sebagai entry point Compose dan memanggil NavGraph-nya.
//
// File ini adalah satu-satunya Activity di aplikasi (Single Activity Architecture).
package com.fishflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.core.ui.theme.FishFlowTheme
import com.fishflow.app.data.model.Order
import com.fishflow.app.feature.seller.orders.SellerOrderDetailScreen
import com.fishflow.app.feature.seller.orders.SellerOrderListScreen
import com.fishflow.app.feature.seller.orders.SellerOrderUiState
import com.fishflow.app.feature.tourist.cart.CartScreen
import com.fishflow.app.feature.tourist.catalog.CatalogDemoHost
import com.fishflow.app.feature.tourist.checkout.CheckoutScreen
import com.fishflow.app.feature.tourist.destination.TourismDemoHost
import com.fishflow.app.feature.tourist.order_history.OrderHistoryScreen
import com.fishflow.app.feature.tourist.order_history.OrderTrackingScreen
import com.fishflow.app.feature.tourist.order_history.sampleTrackingOrder

private sealed interface LauncherRoute {
    data object Menu : LauncherRoute
    data object Cart : LauncherRoute
    data object Checkout : LauncherRoute
    data object OrderHistory : LauncherRoute
    data class OrderTracking(val order: Order = sampleTrackingOrder()) : LauncherRoute
    data object SellerOrders : LauncherRoute
    data class SellerOrderDetail(val order: Order = SellerOrderUiState.sampleSellerOrders().first()) : LauncherRoute
    data object TourismDemo : LauncherRoute
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FishFlowTheme {
                FishFlowSulthonShowcase()
            }
        }
    }
}

@Composable
private fun FishFlowSulthonShowcase() {
    var route by remember { mutableStateOf<LauncherRoute>(LauncherRoute.Menu) }
    // Terpisah dari route karena harus bertahan saat rotasi (state filter katalog).
    var isCatalogOpen by rememberSaveable { mutableStateOf(false) }

    if (isCatalogOpen) {
        CatalogDemoHost(
            onExit = { isCatalogOpen = false },
            onProceedToCheckout = {
                isCatalogOpen = false
                route = LauncherRoute.Checkout
            },
            onNavItemClick = { tab ->
                if (tab != "Katalog") {
                    isCatalogOpen = false
                    route = when (tab) {
                        "Rencana" -> LauncherRoute.TourismDemo
                        "Pesanan" -> LauncherRoute.OrderHistory
                        else -> LauncherRoute.Menu
                    }
                }
            }
        )
        return
    }

    BackHandler(enabled = route != LauncherRoute.Menu) {
        route = when (route) {
            is LauncherRoute.OrderTracking -> LauncherRoute.OrderHistory
            is LauncherRoute.SellerOrderDetail -> LauncherRoute.SellerOrders
            else -> LauncherRoute.Menu
        }
    }

    when (val current = route) {
        LauncherRoute.Menu -> Surface(
            modifier = Modifier.fillMaxSize(),
            color = OrderDesignTokens.CanvasBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(OrderDesignTokens.MaritimeDark)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "FISH FLOW • UI SLICING",
                        color = OrderDesignTokens.MintAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp
                    )
                    Text(
                        text = "Halaman Sulthon",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Pilih layar untuk menguji hasil slicing Jetpack Compose dari Figma.",
                        color = OrderDesignTokens.SageLight,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                LauncherButton("1. CartScreen (Keranjang Wisatawan)") {
                    route = LauncherRoute.Cart
                }
                LauncherButton("2. CheckoutScreen (Checkout Pembayaran)") {
                    route = LauncherRoute.Checkout
                }
                LauncherButton("3. OrderHistoryScreen (Riwayat Pesanan)") {
                    route = LauncherRoute.OrderHistory
                }
                LauncherButton("4. OrderTrackingScreen (Tracking Pesanan)") {
                    route = LauncherRoute.OrderTracking()
                }
                LauncherButton("5. SellerOrderListScreen (Ringkasan Pesanan Penjual)") {
                    route = LauncherRoute.SellerOrders
                }
                LauncherButton("6. SellerOrderDetailScreen (Detail Pesanan Penjual)") {
                    route = LauncherRoute.SellerOrderDetail()
                }
                LauncherButton(
                    label = "Demo F3 Tourism & Budget",
                    containerColor = OrderDesignTokens.SurfaceContainer,
                    contentColor = OrderDesignTokens.TextPrimary
                ) {
                    route = LauncherRoute.TourismDemo
                }
                LauncherButton(
                    label = "Katalog Ikan (F4)",
                    containerColor = OrderDesignTokens.SurfaceContainer,
                    contentColor = OrderDesignTokens.TextPrimary
                ) {
                    isCatalogOpen = true
                }
            }
        }

        LauncherRoute.Cart -> CartScreen(
            onBack = { route = LauncherRoute.Menu },
            onBrowseCatalog = { route = LauncherRoute.Menu },
            onProceedToCheckout = { route = LauncherRoute.Checkout }
        )

        LauncherRoute.Checkout -> CheckoutScreen(
            onBack = { route = LauncherRoute.Menu },
            onConfirmOrder = { route = LauncherRoute.OrderTracking() }
        )

        LauncherRoute.OrderHistory -> OrderHistoryScreen(
            onOrderClick = { order -> route = LauncherRoute.OrderTracking(order) },
            onNavItemClick = { tab ->
                if (tab == "Home") route = LauncherRoute.Menu
            }
        )

        is LauncherRoute.OrderTracking -> OrderTrackingScreen(
            initialOrder = current.order,
            onBack = { route = LauncherRoute.OrderHistory },
            onNavItemClick = { tab ->
                if (tab == "Home") route = LauncherRoute.Menu
            }
        )

        LauncherRoute.SellerOrders -> SellerOrderListScreen(
            onBack = { route = LauncherRoute.Menu },
            onOrderClick = { order -> route = LauncherRoute.SellerOrderDetail(order) },
            onNavClick = { tab ->
                if (tab == "Profil") route = LauncherRoute.Menu
            }
        )

        is LauncherRoute.SellerOrderDetail -> SellerOrderDetailScreen(
            initialOrder = current.order,
            onBack = { route = LauncherRoute.SellerOrders }
        )

        LauncherRoute.TourismDemo -> TourismDemoHost()
    }
}

@Composable
private fun LauncherButton(
    label: String,
    containerColor: Color = OrderDesignTokens.CoastalTeal,
    contentColor: Color = Color.White,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
