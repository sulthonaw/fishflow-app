// CatalogDemoHost.kt
// Developer: Octavia
//
// Host alur Fungsi 4: Katalog -> Detail -> CartScreen (Fungsi 2), selama NavGraph belum ada.
// Posisi layar disimpan dengan rememberSaveable supaya tidak kembali ke awal saat rotasi.
//
// CartScreen saat ini belum punya penyimpanan keranjang (CartViewModel masih kosong), jadi
// yang diteruskan hanya produk yang baru dipilih. Setelah CartViewModel tersedia, ganti isi
// onAddToCart dengan CartViewModel.onAddItem(product, qty).
package com.fishflow.app.feature.tourist.catalog

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.fishflow.app.R
import com.fishflow.app.data.model.FishProduct
import com.fishflow.app.feature.tourist.cart.CartItem
import com.fishflow.app.feature.tourist.cart.CartScreen
import com.fishflow.app.feature.tourist.cart.CartUiState
import com.fishflow.app.feature.tourist.home.TouristDestinationHost

@Composable
fun CatalogDemoHost(
    onExit: () -> Unit,
    onProceedToCheckout: (CartUiState) -> Unit,
    modifier: Modifier = Modifier,
    onNavItemClick: (String) -> Unit = {},
    initialQuery: String? = null,
    initialProductId: String? = null
) {
    var selectedProductId by rememberSaveable { mutableStateOf(initialProductId) }
    var selectedDestinationId by rememberSaveable { mutableStateOf<String?>(null) }
    // Hanya dipakai sekali supaya tidak menimpa filter saat rotasi atau kembali dari detail.
    var pendingQuery by rememberSaveable { mutableStateOf(initialQuery) }
    // Detail yang dibuka langsung dari beranda kembali ke beranda, bukan ke katalog.
    var isOpenedOnDetail by rememberSaveable { mutableStateOf(initialProductId != null) }
    var cartItem by rememberSaveable(stateSaver = CartItemSaver) {
        mutableStateOf<CartItem?>(null)
    }

    val productId = selectedProductId
    val destinationId = selectedDestinationId
    val item = cartItem

    val onBack: () -> Unit = {
        when {
            cartItem != null -> cartItem = null
            selectedProductId != null && !isOpenedOnDetail -> selectedProductId = null
            else -> onExit()
        }
    }

    BackHandler(onBack = onBack)

    when {
        item != null -> CartScreen(
            modifier = modifier,
            initialState = CartUiState(items = listOf(item)),
            onBack = onBack,
            onBrowseCatalog = {
                cartItem = null
                selectedProductId = null
                isOpenedOnDetail = false
            },
            onProceedToCheckout = onProceedToCheckout
        )

        productId != null -> FishDetailScreen(
            productId = productId,
            onAddToCart = { product, quantity -> cartItem = product.toCartItem(quantity) },
            onBack = onBack,
            modifier = modifier
        )

        destinationId != null -> TouristDestinationHost(
            onExit = { selectedDestinationId = null },
            modifier = modifier,
            initialDestinationId = destinationId
        )

        else -> FishCatalogScreen(
            onProductClick = { product -> selectedProductId = product.id },
            onDestinationClick = { destination -> selectedDestinationId = destination.id },
            modifier = modifier,
            onBack = onBack,
            onNavItemClick = onNavItemClick,
            initialQuery = pendingQuery,
            onInitialQueryApplied = { pendingQuery = null }
        )
    }
}

// Keranjang Fungsi 2 memakai kg bulat, jadi stok dibulatkan ke bawah.
internal fun FishProduct.toCartItem(quantity: Int): CartItem = CartItem(
    productId = id,
    namaIkan = namaIkan,
    vesselName = namaKapal ?: namaPenjual,
    qty = quantity,
    hargaSatuan = hargaPerKg,
    availableStockKg = stokTersedia.toInt(),
    imageRes = imageRes ?: R.drawable.img_fishflow_logo
)

private val CartItemSaver = listSaver<CartItem?, Any>(
    save = { item ->
        if (item == null) {
            emptyList()
        } else {
            listOf(
                item.productId,
                item.namaIkan,
                item.vesselName,
                item.qty,
                item.hargaSatuan,
                item.availableStockKg,
                item.badgeLabel,
                item.imageRes
            )
        }
    },
    restore = { saved ->
        if (saved.isEmpty()) {
            null
        } else {
            CartItem(
                productId = saved[0] as String,
                namaIkan = saved[1] as String,
                vesselName = saved[2] as String,
                qty = saved[3] as Int,
                hargaSatuan = saved[4] as Double,
                availableStockKg = saved[5] as Int,
                badgeLabel = saved[6] as String,
                imageRes = saved[7] as Int
            )
        }
    }
)
