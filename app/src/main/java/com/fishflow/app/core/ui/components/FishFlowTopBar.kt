// FishFlowTopBar.kt
// Developer: Octavia
//
// Koneksi: Octavia bikin beranda dan katalog wisatawan yang paling banyak
// pakai komponen UI shared. Dia yang standardisasi tampilan visual app.
//
// Top bar standar FishFlow sesuai Figma: tombol kembali (opsional), logo, judul, aksi di kanan.
// Stateless - semua data dari parameter, tidak pegang state sendiri.
package com.fishflow.app.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishflow.app.R
import com.fishflow.app.core.util.Constants

@Composable
fun FishFlowTopBar(
    title: String,
    modifier: Modifier = Modifier,
    showBrand: Boolean = false,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = { FishFlowProfileImage() }
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = OrderDesignTokens.CanvasBg
    ) {
        Row(
            modifier = Modifier
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Top + WindowInsetsSides.Horizontal
                    )
                )
                .heightIn(min = 64.dp)
                .padding(start = if (onBack != null) 4.dp else 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Kembali",
                        tint = OrderDesignTokens.TextPrimary
                    )
                }
            }
            Image(
                painter = painterResource(id = R.drawable.img_fishflow_logo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Column(modifier = Modifier.weight(1f)) {
                if (showBrand) {
                    Text(
                        text = Constants.APP_NAME,
                        color = OrderDesignTokens.CoastalTeal,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
                Text(
                    text = title,
                    color = OrderDesignTokens.TextPrimary,
                    fontSize = if (showBrand) 16.sp else 19.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            actions()
        }
    }
}

@Composable
fun FishFlowProfileImage(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.img_profile_buyer),
        contentDescription = "Profil",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
    )
}
