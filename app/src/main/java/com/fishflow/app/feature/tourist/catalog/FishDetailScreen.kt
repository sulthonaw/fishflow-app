// FishDetailScreen.kt
// Developer: Octavia
//
// Detail produk ikan (sisi wisatawan).
//
// UI (sesuai Figma "Detail Tangkapan"):
// - Foto besar dengan wilayah, nama ikan, dan kategori
// - Panel isi dengan tab: Tentang Ikan, Info Penjual, Titik Ambil
// - Ringkasan sisa stok, armada, dan batas ambil
// - Pengatur jumlah (QuantityStepper Fungsi 2)
// - Bar bawah: harga per kg + tombol "Buat Pesanan" -> diteruskan ke keranjang Fungsi 2
package com.fishflow.app.feature.tourist.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DirectionsBoat
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Scale
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.ShoppingBasket
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fishflow.app.core.ui.components.ErrorMessage
import com.fishflow.app.core.ui.components.FishFlowTopBar
import com.fishflow.app.core.ui.components.LoadingIndicator
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.core.ui.components.QuantityStepper
import com.fishflow.app.data.model.FishProduct
import com.fishflow.app.feature.tourist.catalog.components.FishProductImage
import com.fishflow.app.feature.tourist.catalog.components.ProductPrice
import com.fishflow.app.feature.tourist.catalog.components.formatStokKg
import com.fishflow.app.feature.tourist.catalog.components.formatWilayah

private enum class DetailTab(val label: String) {
    TENTANG("Tentang Ikan"),
    PENJUAL("Info Penjual"),
    TITIK_AMBIL("Titik Ambil")
}

private val HeroHeight = 260.dp
private val SheetOverlap = 28.dp

@Composable
fun FishDetailScreen(
    uiState: FishDetailUiState,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit,
    onAddToCart: (FishProduct, Int) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val product = uiState.product

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = OrderDesignTokens.CanvasBg,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = { FishFlowTopBar(title = "Detail Tangkapan", onBack = onBack) },
        bottomBar = {
            if (product != null) {
                OrderBar(
                    hargaPerKg = product.hargaPerKg,
                    canOrder = uiState.canOrder,
                    onClick = { onAddToCart(product, uiState.quantity) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            when {
                uiState.isLoading -> LoadingIndicator()

                uiState.errorMessage != null -> ErrorMessage(
                    message = uiState.errorMessage,
                    onRetry = onRetry
                )

                product != null -> FishDetailContent(
                    product = product,
                    quantity = uiState.quantity,
                    maxQuantity = uiState.maxQuantity,
                    onIncreaseQuantity = onIncreaseQuantity,
                    onDecreaseQuantity = onDecreaseQuantity
                )
            }
        }
    }
}

@Composable
fun FishDetailScreen(
    productId: String,
    onAddToCart: (FishProduct, Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FishDetailViewModel = viewModel(
        key = productId,
        factory = FishDetailViewModel.factory(productId)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.load()
    }

    FishDetailScreen(
        uiState = uiState,
        onIncreaseQuantity = viewModel::onIncreaseQuantity,
        onDecreaseQuantity = viewModel::onDecreaseQuantity,
        onAddToCart = onAddToCart,
        onRetry = viewModel::onRetry,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun FishDetailContent(
    product: FishProduct,
    quantity: Int,
    maxQuantity: Int,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit
) {
    var selectedTab by rememberSaveable(product.id) { mutableStateOf(DetailTab.TENTANG) }

    // Panel isi menumpuk di atas bagian bawah foto.
    Box {
        DetailHero(product = product)
        Column(
            modifier = Modifier
                .padding(top = HeroHeight - SheetOverlap)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(OrderDesignTokens.CanvasBg)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(OrderDesignTokens.DividerColor)
            )

            DetailTabs(selectedTab = selectedTab, onSelect = { selectedTab = it })

            when (selectedTab) {
                DetailTab.TENTANG -> AboutFishTab(product = product)
                DetailTab.PENJUAL -> InfoCard {
                    DetailRow(label = "Nama penjual", value = product.namaPenjual)
                    DetailRow(label = "Armada tangkap", value = product.namaKapal ?: "-")
                }

                DetailTab.TITIK_AMBIL -> InfoCard {
                    DetailRow(label = "Lokasi pengambilan", value = product.lokasiPengambilan)
                    DetailRow(label = "Wilayah", value = formatWilayah(product.wilayah))
                    DetailRow(label = "Batas ambil", value = product.batasPengambilan ?: "-")
                }
            }

            QuantitySection(
                product = product,
                quantity = quantity,
                maxQuantity = maxQuantity,
                onIncreaseQuantity = onIncreaseQuantity,
                onDecreaseQuantity = onDecreaseQuantity
            )
        }
    }
}

@Composable
private fun DetailHero(product: FishProduct) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HeroHeight)
    ) {
        FishProductImage(
            namaIkan = product.namaIkan,
            imageRes = product.imageRes,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.35f to Color.Transparent,
                        1f to OrderDesignTokens.ForestDeep
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, end = 16.dp, bottom = SheetOverlap + 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(OrderDesignTokens.CoastalTeal)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = null,
                    tint = OrderDesignTokens.MintAccent,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "${formatWilayah(product.wilayah)}, Blitar",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = product.namaIkan,
                color = Color.White,
                fontSize = 22.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = product.kategori.label,
                color = OrderDesignTokens.MintAccent,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun DetailTabs(
    selectedTab: DetailTab,
    onSelect: (DetailTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(OrderDesignTokens.SurfaceContainer)
            .padding(4.dp)
            .selectableGroup()
    ) {
        DetailTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.White else Color.Transparent)
                    .selectable(
                        selected = isSelected,
                        role = Role.Tab,
                        onClick = { onSelect(tab) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab.label,
                    color = if (isSelected) {
                        OrderDesignTokens.CoastalTeal
                    } else {
                        OrderDesignTokens.TextSecondary
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun AboutFishTab(product: FishProduct) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            icon = Icons.Rounded.Scale,
            label = "SISA STOK",
            value = formatStokKg(product.stokTersedia),
            valueColor = OrderDesignTokens.CoastalTeal,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Rounded.DirectionsBoat,
            label = "ARMADA",
            value = product.namaKapal ?: "-",
            valueColor = OrderDesignTokens.TextPrimary,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Rounded.Schedule,
            label = "BATAS AMBIL",
            value = product.batasPengambilan ?: "-",
            valueColor = OrderDesignTokens.WarningText,
            modifier = Modifier.weight(1f)
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Info,
                contentDescription = null,
                tint = OrderDesignTokens.CoastalTeal,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "Kualitas & Penanganan Tangkapan",
                color = OrderDesignTokens.MaritimeDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = product.infoPenanganan ?: "Informasi penanganan belum tersedia.",
            color = OrderDesignTokens.TextSecondary,
            fontSize = 13.sp,
            lineHeight = 20.sp
        )
    }

    SellerCard(product = product)

    FreshnessGuarantee()
}

@Composable
private fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(16.dp))
            .background(OrderDesignTokens.SurfaceContainer)
            .padding(horizontal = 6.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = valueColor,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            color = OrderDesignTokens.TextSecondary,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Text(
            text = value,
            color = valueColor,
            fontSize = 13.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SellerCard(product: FishProduct) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(OrderDesignTokens.MintAccent.copy(alpha = 0.35f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(OrderDesignTokens.CoastalTeal),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = product.namaPenjual.take(1).uppercase(),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Column {
            Text(
                text = product.namaPenjual,
                color = OrderDesignTokens.MaritimeDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = product.namaKapal?.let { "Penjual • $it" } ?: "Penjual",
                color = OrderDesignTokens.TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun FreshnessGuarantee() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(OrderDesignTokens.SurfaceContainer)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(OrderDesignTokens.MintAccent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.VerifiedUser,
                contentDescription = null,
                tint = OrderDesignTokens.CoastalTeal,
                modifier = Modifier.size(20.dp)
            )
        }
        Column {
            Text(
                text = "Jaminan Kesegaran 100%",
                color = OrderDesignTokens.MaritimeDark,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Ganti baru jika aroma & tekstur tidak sesuai ekspektasi.",
                color = OrderDesignTokens.TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun InfoCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        content = content
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = label,
            color = OrderDesignTokens.TextMuted,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            color = OrderDesignTokens.TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuantitySection(
    product: FishProduct,
    quantity: Int,
    maxQuantity: Int,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit
) {
    when {
        maxQuantity >= 1 -> QuantityStepper(
            quantity = quantity,
            onIncrease = onIncreaseQuantity,
            onDecrease = onDecreaseQuantity,
            maxQuantity = maxQuantity,
            subtitle = null
        )

        product.isAvailable -> Text(
            text = "Sisa stok kurang dari 1 kg, belum bisa dipesan.",
            color = OrderDesignTokens.ErrorText,
            fontSize = 13.sp
        )

        else -> Text(
            text = "Produk belum tersedia",
            color = OrderDesignTokens.ErrorText,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun OrderBar(
    hargaPerKg: Double,
    canOrder: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "HARGA SATUAN",
                    color = OrderDesignTokens.TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
                ProductPrice(hargaPerKg = hargaPerKg, fontSize = 20)
            }
            Button(
                onClick = onClick,
                enabled = canOrder,
                shape = CircleShape,
                modifier = Modifier.heightIn(min = 48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrderDesignTokens.CoastalTeal,
                    contentColor = Color.White,
                    disabledContainerColor = OrderDesignTokens.SurfaceContainer,
                    disabledContentColor = OrderDesignTokens.TextMuted
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.ShoppingBasket,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Buat Pesanan", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
