package com.fishflow.app.feature.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.DirectionsBoat
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishflow.app.R
import com.fishflow.app.core.ui.components.AdminBottomBarItems
import com.fishflow.app.core.ui.components.CoastalHeroHeader
import com.fishflow.app.core.ui.components.FishFlowBottomBar
import com.fishflow.app.core.ui.components.FishFlowTopBar
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.core.ui.components.SellerBottomBarItems
import com.fishflow.app.core.ui.components.TouristBottomBarItems
import com.fishflow.app.data.model.UserRole

@Composable
fun ProfileScreen(
    userName: String,
    userEmail: String,
    userRole: UserRole,
    onNavItemClick: (String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bottomItems = when (userRole) {
        UserRole.WISATAWAN -> TouristBottomBarItems
        UserRole.PENJUAL -> SellerBottomBarItems
        UserRole.ADMIN -> AdminBottomBarItems
    }

    val roleTitle = when (userRole) {
        UserRole.WISATAWAN -> "Wisatawan Pesisir"
        UserRole.PENJUAL -> "Penjual / Nelayan Mitra"
        UserRole.ADMIN -> "Administrator FishFlow"
    }

    val roleIcon = when (userRole) {
        UserRole.WISATAWAN -> Icons.Rounded.Explore
        UserRole.PENJUAL -> Icons.Rounded.DirectionsBoat
        UserRole.ADMIN -> Icons.Rounded.AdminPanelSettings
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = OrderDesignTokens.CanvasBg,
        topBar = {
            FishFlowTopBar(
                title = "Profil Akun",
                showBrand = true
            )
        },
        bottomBar = {
            FishFlowBottomBar(
                items = bottomItems,
                currentRoute = "Profil",
                onNavigate = onNavItemClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            CoastalHeroHeader(
                badgeText = roleTitle,
                title = userName,
                subtitle = userEmail,
                trailingContent = {
                    Image(
                        painter = painterResource(
                            id = if (userRole == UserRole.PENJUAL) {
                                R.drawable.img_profile_seller
                            } else {
                                R.drawable.img_profile_buyer
                            }
                        ),
                        contentDescription = userName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                    )
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Informasi Sesi Aktif",
                            color = OrderDesignTokens.TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        ProfileDetailRow(
                            icon = Icons.Rounded.Email,
                            label = "Email",
                            value = userEmail
                        )
                        ProfileDetailRow(
                            icon = roleIcon,
                            label = "Peran Aktif",
                            value = roleTitle
                        )
                    }
                }

                Button(
                    onClick = onLogout,
                    shape = RoundedCornerShape(9999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrderDesignTokens.ErrorBg,
                        contentColor = OrderDesignTokens.ErrorText
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Keluar dari Akun",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(OrderDesignTokens.SurfaceMuted)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(OrderDesignTokens.MintAccent.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = OrderDesignTokens.CoastalTealDark,
                modifier = Modifier.size(18.dp)
            )
        }
        Column {
            Text(
                text = label,
                color = OrderDesignTokens.TextMuted,
                fontSize = 11.sp
            )
            Text(
                text = value,
                color = OrderDesignTokens.TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
