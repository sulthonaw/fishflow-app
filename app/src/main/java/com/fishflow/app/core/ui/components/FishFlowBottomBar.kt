// FishFlowBottomBar.kt
// Developer: Octavia
//
// Koneksi: Bottom nav muncul di beranda wisatawan (screen Octavia)
// dan dashboard penjual. Octavia yang desain dan standardisasi.
//
// Bottom navigation bar. Terima daftar menu dan currentRoute, panggil onNavigate saat diklik.
// Menu berbeda tergantung role (Penjual vs Wisatawan).
// Stateless — route items dan callback dari parameter.
//
// Tampilan dan menu wisatawan mengikuti TouristOrderBottomBar (Sulthon) dan Figma.
// Menu penjual belum ditentukan. Route masih memakai nama menu selama NavGraph belum ada.
package com.fishflow.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.Landscape
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishflow.app.core.ui.theme.FishFlowTheme

data class FishFlowBottomBarItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val TouristBottomBarItems: List<FishFlowBottomBarItem> = listOf(
    FishFlowBottomBarItem(route = "Home", label = "Home", icon = Icons.Rounded.Home),
    FishFlowBottomBarItem(route = "Katalog", label = "Katalog", icon = Icons.Rounded.Storefront),
    FishFlowBottomBarItem(route = "Rencana", label = "Rencana", icon = Icons.Rounded.CalendarMonth),
    FishFlowBottomBarItem(
        route = "Pesanan",
        label = "Pesanan",
        icon = Icons.AutoMirrored.Rounded.ReceiptLong
    ),
    FishFlowBottomBarItem(route = "Profil", label = "Profil", icon = Icons.Rounded.Person)
)

val SellerBottomBarItems: List<FishFlowBottomBarItem> = listOf(
    FishFlowBottomBarItem(route = "Dashboard", label = "Dashboard", icon = Icons.Rounded.Dashboard),
    FishFlowBottomBarItem(route = "Stok", label = "Stok", icon = Icons.Rounded.Inventory2),
    FishFlowBottomBarItem(route = "Profil", label = "Profil", icon = Icons.Rounded.Person)
)

val AdminBottomBarItems: List<FishFlowBottomBarItem> = listOf(
    FishFlowBottomBarItem(route = "Dashboard", label = "Dashboard", icon = Icons.Rounded.Dashboard),
    FishFlowBottomBarItem(route = "Profil", label = "Profil", icon = Icons.Rounded.Person)
)

@Composable
fun FishFlowBottomBar(
    items: List<FishFlowBottomBarItem>,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = OrderDesignTokens.MaritimeDark,
        shadowElevation = 8.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .selectableGroup(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                BottomBarItem(
                    item = item,
                    selected = item.route == currentRoute,
                    onClick = { onNavigate(item.route) }
                )
            }
        }
    }
}

@Composable
private fun BottomBarItem(
    item: FishFlowBottomBarItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val contentColor =
        if (selected) OrderDesignTokens.MintAccent else OrderDesignTokens.SageMuted

    Column(
        modifier = Modifier
            .clip(CircleShape)
            .background(
                if (selected) OrderDesignTokens.MintAccent.copy(alpha = 0.2f) else Color.Transparent
            )
            .selectable(selected = selected, role = Role.Tab, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = item.label,
            color = contentColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
private fun FishFlowBottomBarPreview() {
    FishFlowTheme {
        FishFlowBottomBar(
            items = TouristBottomBarItems,
            currentRoute = "Katalog",
            onNavigate = {}
        )
    }
}
