// PickupCodeCard.kt
// Developer: Sulthon
// Kartu kode pengambilan (QR code atau token alfanumerik).
// Muncul hanya setelah status SIAP_DIAMBIL.
// Stateless — terima kodePickup (String).
package com.fishflow.app.feature.tourist.order_history.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishflow.app.core.ui.components.OrderDesignTokens

@Composable
fun PickupCodeCard(
    kodePickup: String,
    modifier: Modifier = Modifier,
    sellerLabel: String = "Pak Joko (Penjual Sadranan)"
) {
    val context = LocalContext.current
    var copied by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Icon(
                imageVector = Icons.Rounded.QrCode2,
                contentDescription = null,
                tint = OrderDesignTokens.SurfaceContainer,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(56.dp)
                    .alpha(0.55f)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(OrderDesignTokens.SurfaceContainer)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = OrderDesignTokens.TextSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Kode Pengambilan Resmi",
                        color = OrderDesignTokens.TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tunjukkan Kode ke Penjual",
                    color = OrderDesignTokens.ForestDeep,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.19).sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 1.dp
                ) {
                    Box(modifier = Modifier.padding(12.dp)) {
                        DeterministicQrCanvas(
                            seed = kodePickup,
                            modifier = Modifier.size(160.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .widthIn(max = 260.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(OrderDesignTokens.SurfaceContainer)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (copied) "TERSALIN" else "BACKUP PIN",
                            color = if (copied) OrderDesignTokens.CoastalTeal else OrderDesignTokens.TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = kodePickup,
                            color = OrderDesignTokens.ForestDeep,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.9.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White,
                        shadowElevation = 1.dp,
                        modifier = Modifier.size(44.dp)
                    ) {
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                clipboard?.setPrimaryClip(ClipData.newPlainText("Backup PIN", kodePickup))
                                copied = true
                            }
                        ) {
                            Icon(
                                imageVector = if (copied) Icons.Rounded.Check else Icons.Rounded.ContentCopy,
                                contentDescription = "Salin Kode PIN",
                                tint = OrderDesignTokens.CoastalTeal,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = buildAnnotatedString {
                        append("Tunjukkan QR Code atau sebutkan PIN ke ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = OrderDesignTokens.TextPrimary)) {
                            append(sellerLabel)
                        }
                        append(" saat serah terima ikan di pos dermaga.")
                    },
                    color = OrderDesignTokens.TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 290.dp)
                )
            }
        }
    }
}

@Composable
private fun DeterministicQrCanvas(
    seed: String,
    modifier: Modifier = Modifier
) {
    val gridSize = 21
    val matrix = remember(seed) { buildQrMatrix(seed, gridSize) }

    Canvas(modifier = modifier) {
        val cellSize = size.width / gridSize
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                if (matrix[row][col]) {
                    val isCenterAccent = row in 9..11 && col in 9..11
                    drawRect(
                        color = if (isCenterAccent) OrderDesignTokens.CoastalTeal else OrderDesignTokens.ForestDeep,
                        topLeft = Offset(col * cellSize, row * cellSize),
                        size = Size(cellSize * 0.96f, cellSize * 0.96f)
                    )
                }
            }
        }
    }
}

private fun buildQrMatrix(seed: String, n: Int): Array<BooleanArray> {
    val grid = Array(n) { BooleanArray(n) }

    fun drawFinder(startRow: Int, startCol: Int) {
        for (r in 0 until 7) {
            for (c in 0 until 7) {
                val border = r == 0 || r == 6 || c == 0 || c == 6
                val center = r in 2..4 && c in 2..4
                grid[startRow + r][startCol + c] = border || center
            }
        }
    }

    drawFinder(0, 0)
    drawFinder(0, n - 7)
    drawFinder(n - 7, 0)

    var hash = seed.hashCode()
    for (r in 0 until n) {
        for (c in 0 until n) {
            val inTopLeft = r < 8 && c < 8
            val inTopRight = r < 8 && c >= n - 8
            val inBottomLeft = r >= n - 8 && c < 8
            if (!inTopLeft && !inTopRight && !inBottomLeft) {
                hash = hash * 31 + (r * 17 + c * 13)
                grid[r][c] = (hash and 1) == 0
            }
        }
    }

    return grid
}
