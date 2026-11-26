// TouristHomeDemoHost.kt
// Developer: Octavia
//
// Host alur beranda wisatawan: Beranda -> Katalog / Detail / Destinasi, selama NavGraph belum ada.
// Tab Rencana, Pesanan, dan Profil diteruskan ke pemanggil lewat onNavItemClick.
package com.fishflow.app.feature.tourist.home

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.fishflow.app.feature.tourist.cart.CartUiState
import com.fishflow.app.feature.tourist.catalog.CatalogDemoHost

@Composable
fun TouristHomeDemoHost(
    onExit: () -> Unit,
    onProceedToCheckout: (CartUiState) -> Unit,
    onNavItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDestinationOpen by rememberSaveable { mutableStateOf(false) }
    var selectedDestinationId by rememberSaveable { mutableStateOf<String?>(null) }
    var isCatalogOpen by rememberSaveable { mutableStateOf(false) }
    var catalogQuery by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedProductId by rememberSaveable { mutableStateOf<String?>(null) }

    val openCatalog: (String?) -> Unit = { query ->
        catalogQuery = query
        selectedProductId = null
        isCatalogOpen = true
    }

    if (isCatalogOpen) {
        CatalogDemoHost(
            onExit = { isCatalogOpen = false },
            onProceedToCheckout = onProceedToCheckout,
            modifier = modifier,
            onNavItemClick = { tab ->
                when (tab) {
                    "Home" -> isCatalogOpen = false
                    "Katalog" -> Unit
                    else -> onNavItemClick(tab)
                }
            },
            initialQuery = catalogQuery,
            initialProductId = selectedProductId
        )
        return
    }

    if (isDestinationOpen) {
        TouristDestinationHost(
            onExit = { isDestinationOpen = false },
            modifier = modifier,
            initialDestinationId = selectedDestinationId
        )
        return
    }

    BackHandler(onBack = onExit)

    TouristHomeScreen(
        onSearch = openCatalog,
        onTourismClick = {
            selectedDestinationId = null
            isDestinationOpen = true
        },
        onSeeAllClick = { openCatalog("") },
        onProductClick = { product ->
            catalogQuery = null
            selectedProductId = product.id
            isCatalogOpen = true
        },
        onDestinationClick = { destination ->
            selectedDestinationId = destination.id
            isDestinationOpen = true
        },
        onNavItemClick = { tab ->
            when (tab) {
                "Home" -> Unit
                "Katalog" -> openCatalog(null)
                else -> onNavItemClick(tab)
            }
        },
        modifier = modifier
    )
}
