package com.fishflow.app.navigation

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
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

object BottomNavItems {
    val tourist = listOf(
        BottomNavItem(Screen.TouristHome.route, "Home", Icons.Rounded.Home),
        BottomNavItem(Screen.FishCatalog.createRoute(), "Katalog", Icons.Rounded.Storefront),
        BottomNavItem(Screen.Destinations.route, "Rencana", Icons.Rounded.CalendarMonth),
        BottomNavItem(Screen.OrderHistory.route, "Pesanan", Icons.AutoMirrored.Rounded.ReceiptLong),
        BottomNavItem(Screen.TouristProfile.route, "Profil", Icons.Rounded.Person)
    )

    val seller = listOf(
        BottomNavItem(Screen.SellerDashboard.route, "Dashboard", Icons.Rounded.Dashboard),
        BottomNavItem(Screen.Inventory.route, "Stok", Icons.Rounded.Inventory2),
        BottomNavItem(Screen.SellerProfile.route, "Profil", Icons.Rounded.Person)
    )

    val admin = listOf(
        BottomNavItem(Screen.AdminDashboard.route, "Dashboard", Icons.Rounded.Dashboard),
        BottomNavItem(Screen.AdminProfile.route, "Profil", Icons.Rounded.Person)
    )
}
