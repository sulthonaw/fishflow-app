package com.fishflow.app.feature.seller.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fishflow.app.core.ui.components.CoastalHeroHeader
import com.fishflow.app.core.ui.components.ErrorToastEffect
import com.fishflow.app.core.ui.components.FishFlowTopBar
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.core.util.CurrencyFormatter
import com.fishflow.app.data.model.FishProduct

@Composable
fun UpdatePriceScreen(
    product: FishProduct,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UpdatePriceViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ErrorToastEffect(
        errorMessage = uiState.errorMessage,
        onConsumed = viewModel::clearMessages
    )

    LaunchedEffect(product.id) {
        viewModel.loadProduct(product)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = OrderDesignTokens.CanvasBg,
        topBar = {
            FishFlowTopBar(
                title = "Perbarui Harga",
                onBack = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            CoastalHeroHeader(
                badgeText = "ID Produk: ${uiState.productId}",
                title = uiState.namaIkan,
                subtitle = "Perbarui harga jual per kilogram untuk etalase wisatawan."
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Harga Saat Ini",
                            color = OrderDesignTokens.TextSecondary,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "${CurrencyFormatter.formatRupiah(uiState.hargaLama)} / kg",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrderDesignTokens.ForestDeep
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.hargaBaru,
                            onValueChange = viewModel::onPriceChange,
                            label = { Text("Harga Baru (Rp)") },
                            placeholder = { Text("Contoh: 50000") },
                            supportingText = { Text("Masukkan harga baru dalam Rupiah per kg.") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrderDesignTokens.CoastalTeal,
                                unfocusedBorderColor = OrderDesignTokens.DividerColor
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = !uiState.isSaving
                        )

                        if (uiState.hargaBaruValue != null && uiState.isPriceValid) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(OrderDesignTokens.MintAccent.copy(alpha = 0.35f))
                                    .padding(14.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Preview Harga Baru",
                                        fontSize = 12.sp,
                                        color = OrderDesignTokens.CoastalTealDark,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${CurrencyFormatter.formatRupiah(uiState.hargaBaruValue!!)} / kg",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OrderDesignTokens.ForestDeep
                                    )
                                }
                            }
                        }

                        uiState.successMessage?.let { success ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(OrderDesignTokens.MintAccent.copy(alpha = 0.5f))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = success,
                                    color = OrderDesignTokens.CoastalTealDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Button(
                            onClick = viewModel::savePrice,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(9999.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OrderDesignTokens.CoastalTeal,
                                contentColor = Color.White
                            ),
                            enabled = uiState.canSave
                        ) {
                            if (uiState.isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                            } else {
                                Text("Simpan Perubahan", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
