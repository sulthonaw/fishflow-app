package com.fishflow.app.feature.tourist.order_history.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.draw.clip
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
    sellerLabel: String = "Penjual di Dermaga"
) {
    val context = LocalContext.current
    var copied by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
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
                text = "Sebutkan Kode ke Penjual",
                color = OrderDesignTokens.ForestDeep,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.19).sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(OrderDesignTokens.MintAccent.copy(alpha = 0.35f))
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (copied) "TERSALIN" else "KODE VERIFIKASI",
                        color = OrderDesignTokens.CoastalTealDark,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = kodePickup,
                        color = OrderDesignTokens.ForestDeep,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.2.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White,
                    shadowElevation = 1.dp,
                    modifier = Modifier.size(44.dp)
                ) {
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            clipboard?.setPrimaryClip(ClipData.newPlainText("Kode Pengambilan", kodePickup))
                            copied = true
                        }
                    ) {
                        Icon(
                            imageVector = if (copied) Icons.Rounded.Check else Icons.Rounded.ContentCopy,
                            contentDescription = "Salin Kode Pengambilan",
                            tint = OrderDesignTokens.CoastalTeal,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = buildAnnotatedString {
                    append("Tunjukkan atau sebutkan kode ini ke ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = OrderDesignTokens.TextPrimary)) {
                        append(sellerLabel)
                    }
                    append(" untuk verifikasi serah terima ikan di dermaga.")
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
