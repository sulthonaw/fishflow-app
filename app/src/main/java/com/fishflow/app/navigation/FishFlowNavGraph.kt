package com.fishflow.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.fishflow.app.R
import com.fishflow.app.data.model.FishProduct
import com.fishflow.app.data.model.Order
import com.fishflow.app.data.model.OrderItem
import com.fishflow.app.data.model.OrderStatus
import com.fishflow.app.data.model.ProductStatus
import com.fishflow.app.data.model.UserRole
import com.fishflow.app.feature.admin.AdminDashboardScreen
import com.fishflow.app.feature.admin.ManageDestinationScreen
import com.fishflow.app.feature.admin.ManageUsersScreen
import com.fishflow.app.feature.auth.LoginScreen
import com.fishflow.app.feature.auth.ProfileScreen
import com.fishflow.app.feature.auth.RegisterScreen
import com.fishflow.app.feature.seller.`catch`.AddCatchScreen
import com.fishflow.app.feature.seller.dashboard.SellerDashboardScreen
import com.fishflow.app.feature.seller.inventory.InventoryScreen
import com.fishflow.app.feature.seller.inventory.ProductDetailScreen
import com.fishflow.app.feature.seller.inventory.UpdatePriceScreen
import com.fishflow.app.feature.seller.orders.SellerOrderDetailScreen
import com.fishflow.app.feature.seller.orders.SellerOrderListScreen
import com.fishflow.app.feature.seller.orders.SellerOrderUiState
import com.fishflow.app.feature.tourist.budget.BudgetPlannerScreen
import com.fishflow.app.feature.tourist.cart.CartItem
import com.fishflow.app.feature.tourist.cart.CartScreen
import com.fishflow.app.feature.tourist.cart.CartUiState
import com.fishflow.app.feature.tourist.catalog.FishCatalogScreen
import com.fishflow.app.feature.tourist.catalog.FishDetailScreen
import com.fishflow.app.feature.tourist.checkout.CheckoutScreen
import com.fishflow.app.feature.tourist.checkout.CheckoutUiState
import com.fishflow.app.feature.tourist.destination.DestinationDetailScreen
import com.fishflow.app.feature.tourist.destination.DestinationListScreen
import com.fishflow.app.feature.tourist.home.TouristHomeScreen
import com.fishflow.app.feature.tourist.order_history.OrderHistoryScreen
import com.fishflow.app.feature.tourist.order_history.OrderTrackingScreen
import com.fishflow.app.feature.tourist.order_history.sampleTouristOrders
import com.fishflow.app.feature.tourist.order_history.sampleTrackingOrder
import java.net.URLDecoder

private data class SessionUser(
    val name: String = "Bima Arya",
    val email: String = "wisatawan@fishflow.com",
    val role: UserRole = UserRole.WISATAWAN
)

@Composable
fun FishFlowNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    var sessionUser by remember { mutableStateOf(SessionUser()) }
    var cartState by remember { mutableStateOf(CartUiState()) }
    var checkoutState by remember { mutableStateOf(CheckoutUiState()) }
    var touristOrders by remember { mutableStateOf(sampleTouristOrders()) }
    var selectedSellerProduct by remember {
        mutableStateOf(
            FishProduct(
                id = "prod-tuna-01",
                sellerId = "seller-joko",
                namaIkan = "Tuna Sirip Kuning Super",
                hargaPerKg = 45_000.0,
                stokTersedia = 18.0,
                stokTeralokasi = 3.0,
                wilayah = "Dermaga Sadranan",
                status = ProductStatus.AKTIF
            )
        )
    }

    val navigateAfterAuth: (UserRole, String, String) -> Unit = { role, name, email ->
        sessionUser = SessionUser(name = name, email = email, role = role)
        val targetGraph = when (role) {
            UserRole.WISATAWAN -> Screen.TouristGraph.route
            UserRole.PENJUAL -> Screen.SellerGraph.route
            UserRole.ADMIN -> Screen.AdminGraph.route
        }
        navController.navigate(targetGraph) {
            popUpTo(Screen.AuthGraph.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    val logoutToAuth: () -> Unit = {
        navController.navigate(Screen.AuthGraph.route) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }

    val onTouristBottomNav: (String) -> Unit = { tab ->
        val targetRoute = when (tab) {
            "Home" -> Screen.TouristHome.route
            "Katalog" -> Screen.FishCatalog.createRoute()
            "Rencana" -> Screen.Destinations.route
            "Pesanan" -> Screen.OrderHistory.route
            "Profil" -> Screen.TouristProfile.route
            else -> Screen.TouristHome.route
        }
        navController.navigate(targetRoute) {
            popUpTo(Screen.TouristHome.route) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    val onSellerBottomNav: (String) -> Unit = { tab ->
        val targetRoute = when (tab) {
            "Dashboard" -> Screen.SellerDashboard.route
            "Stok" -> Screen.Inventory.route
            "Profil" -> Screen.SellerProfile.route
            else -> Screen.SellerDashboard.route
        }
        navController.navigate(targetRoute) {
            popUpTo(Screen.SellerDashboard.route) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    val onAdminBottomNav: (String) -> Unit = { tab ->
        val targetRoute = when (tab) {
            "Dashboard" -> Screen.AdminDashboard.route
            "Profil" -> Screen.AdminProfile.route
            else -> Screen.AdminDashboard.route
        }
        navController.navigate(targetRoute) {
            popUpTo(Screen.AdminDashboard.route) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.AuthGraph.route,
        modifier = modifier
    ) {
        navigation(
            startDestination = Screen.Login.route,
            route = Screen.AuthGraph.route
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = navigateAfterAuth,
                    onRegisterClick = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = navigateAfterAuth,
                    onLoginClick = {
                        navController.popBackStack()
                    }
                )
            }
        }

        navigation(
            startDestination = Screen.TouristHome.route,
            route = Screen.TouristGraph.route
        ) {
            composable(Screen.TouristHome.route) {
                TouristHomeScreen(
                    onSearch = { query ->
                        navController.navigate(Screen.FishCatalog.createRoute(query))
                    },
                    onTourismClick = {
                        navController.navigate(Screen.Destinations.route)
                    },
                    onSeeAllClick = {
                        navController.navigate(Screen.FishCatalog.createRoute())
                    },
                    onProductClick = { product ->
                        navController.navigate(Screen.FishDetail.createRoute(product.id))
                    },
                    onDestinationClick = { destination ->
                        navController.navigate(Screen.DestinationDetail.createRoute(destination.id))
                    },
                    onNavItemClick = onTouristBottomNav
                )
            }

            composable(
                route = Screen.FishCatalog.route,
                arguments = listOf(
                    navArgument(Screen.FishCatalog.ARG_QUERY) {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val rawQuery = backStackEntry.arguments?.getString(Screen.FishCatalog.ARG_QUERY)
                val decodedQuery = rawQuery?.let { URLDecoder.decode(it, "UTF-8") }
                FishCatalogScreen(
                    onProductClick = { product ->
                        navController.navigate(Screen.FishDetail.createRoute(product.id))
                    },
                    onDestinationClick = { destination ->
                        navController.navigate(Screen.DestinationDetail.createRoute(destination.id))
                    },
                    onBack = if (decodedQuery != null) ({ navController.popBackStack() }) else null,
                    onNavItemClick = onTouristBottomNav,
                    initialQuery = decodedQuery
                )
            }

            composable(
                route = Screen.FishDetail.route,
                arguments = listOf(
                    navArgument(Screen.FishDetail.ARG_PRODUCT_ID) {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString(Screen.FishDetail.ARG_PRODUCT_ID).orEmpty()
                FishDetailScreen(
                    productId = productId,
                    onAddToCart = { product, quantity ->
                        val newItem = CartItem(
                            productId = product.id,
                            namaIkan = product.namaIkan,
                            vesselName = product.namaKapal ?: product.namaPenjual,
                            qty = quantity,
                            hargaSatuan = product.hargaPerKg,
                            availableStockKg = product.stokTersedia.toInt().coerceAtLeast(1),
                            imageRes = product.imageRes ?: R.drawable.img_tuna_super
                        )
                        val existing = cartState.items.filterNot { it.productId == newItem.productId }
                        cartState = cartState.copy(items = listOf(newItem) + existing)
                        navController.navigate(Screen.Cart.route)
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Destinations.route) {
                DestinationListScreen(
                    onDestinationClick = { destination ->
                        navController.navigate(Screen.DestinationDetail.createRoute(destination.id))
                    },
                    onBack = null,
                    onNavItemClick = onTouristBottomNav
                )
            }

            composable(
                route = Screen.DestinationDetail.route,
                arguments = listOf(
                    navArgument(Screen.DestinationDetail.ARG_DESTINATION_ID) {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val destinationId = backStackEntry.arguments?.getString(Screen.DestinationDetail.ARG_DESTINATION_ID).orEmpty()
                DestinationDetailScreen(
                    destinationId = destinationId,
                    onPlanBudget = { destination, selectedIds ->
                        navController.navigate(
                            Screen.BudgetPlanner.createRoute(
                                destinationId = destination.id,
                                destinationName = destination.nama,
                                selectedIds = selectedIds
                            )
                        )
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.BudgetPlanner.route,
                arguments = listOf(
                    navArgument(Screen.BudgetPlanner.ARG_DESTINATION_ID) { type = NavType.StringType },
                    navArgument(Screen.BudgetPlanner.ARG_DESTINATION_NAME) { type = NavType.StringType },
                    navArgument(Screen.BudgetPlanner.ARG_SELECTED_IDS) {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val destinationId = backStackEntry.arguments?.getString(Screen.BudgetPlanner.ARG_DESTINATION_ID).orEmpty()
                val destinationName = URLDecoder.decode(
                    backStackEntry.arguments?.getString(Screen.BudgetPlanner.ARG_DESTINATION_NAME).orEmpty(),
                    "UTF-8"
                )
                val rawIds = URLDecoder.decode(
                    backStackEntry.arguments?.getString(Screen.BudgetPlanner.ARG_SELECTED_IDS).orEmpty(),
                    "UTF-8"
                )
                val selectedIds = rawIds.split(",").filter { it.isNotBlank() }.toSet()

                BudgetPlannerScreen(
                    destinationId = destinationId,
                    destinationName = destinationName,
                    initialSelectedIds = selectedIds,
                    onChangeSelection = { navController.popBackStack() },
                    onDone = {
                        navController.navigate(Screen.TouristHome.route) {
                            popUpTo(Screen.TouristHome.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screen.Cart.route) {
                CartScreen(
                    initialState = cartState,
                    onBack = { navController.popBackStack() },
                    onBrowseCatalog = {
                        navController.navigate(Screen.FishCatalog.createRoute())
                    },
                    onProceedToCheckout = { updatedCart ->
                        cartState = updatedCart
                        val orderItems = updatedCart.items.mapIndexed { idx, item ->
                            OrderItem(
                                id = "oi-$idx-${item.productId}",
                                orderId = "ord-draft",
                                productId = item.productId,
                                namaIkan = item.namaIkan,
                                qty = item.qty.toDouble(),
                                hargaSatuan = item.hargaSatuan,
                                subtotal = item.subtotal,
                                vesselName = item.vesselName,
                                availableStockKg = item.availableStockKg.toDouble(),
                                imageRes = item.imageRes
                            )
                        }
                        checkoutState = CheckoutUiState(
                            buyerName = sessionUser.name,
                            orderItems = orderItems.ifEmpty { CheckoutUiState.sampleOrderItems() }
                        )
                        navController.navigate(Screen.Checkout.route)
                    }
                )
            }

            composable(Screen.Checkout.route) {
                CheckoutScreen(
                    initialState = checkoutState,
                    onBack = { navController.popBackStack() },
                    onConfirmOrder = { confirmed ->
                        val newOrderId = "ord-ff-${(100..999).random()}"
                        val newOrder = Order(
                            id = newOrderId,
                            orderCode = "#FF-20261126-${(100..999).random()}",
                            buyerId = "buyer-local",
                            buyerName = confirmed.buyerName,
                            buyerSegment = "WISATAWAN DOMESTIK",
                            sellerId = "seller-joko",
                            sellerName = "Pak Joko Widodo",
                            vesselName = confirmed.primaryItem?.vesselName ?: "KM Samudera 02",
                            pickupPoint = confirmed.pickupPoint,
                            items = confirmed.orderItems,
                            extraFee = confirmed.biayaTambahan,
                            totalHarga = confirmed.totalHarga,
                            status = OrderStatus.MENUNGGU_KONFIRMASI,
                            kodePickup = "FF-${(1000..9999).random()}",
                            createdAt = "Baru saja"
                        )
                        touristOrders = listOf(newOrder) + touristOrders
                        cartState = CartUiState(items = emptyList())
                        navController.navigate(Screen.OrderTracking.createRoute(newOrder.id)) {
                            popUpTo(Screen.TouristHome.route) { inclusive = false }
                        }
                    }
                )
            }

            composable(Screen.OrderHistory.route) {
                OrderHistoryScreen(
                    orders = touristOrders,
                    onOrderClick = { order ->
                        navController.navigate(Screen.OrderTracking.createRoute(order.id))
                    },
                    onNavItemClick = onTouristBottomNav
                )
            }

            composable(
                route = Screen.OrderTracking.route,
                arguments = listOf(
                    navArgument(Screen.OrderTracking.ARG_ORDER_ID) {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString(Screen.OrderTracking.ARG_ORDER_ID)
                val order = touristOrders.firstOrNull { it.id == orderId } ?: sampleTrackingOrder()
                OrderTrackingScreen(
                    initialOrder = order,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.TouristProfile.route) {
                ProfileScreen(
                    userName = sessionUser.name,
                    userEmail = sessionUser.email,
                    userRole = UserRole.WISATAWAN,
                    onNavItemClick = onTouristBottomNav,
                    onLogout = logoutToAuth
                )
            }
        }

        navigation(
            startDestination = Screen.SellerDashboard.route,
            route = Screen.SellerGraph.route
        ) {
            composable(Screen.SellerDashboard.route) {
                SellerDashboardScreen(
                    onTambahTangkapan = { navController.navigate(Screen.AddCatch.route) },
                    onPerbaruiHarga = { navController.navigate(Screen.Inventory.route) },
                    onInventoryClick = { navController.navigate(Screen.Inventory.route) },
                    onPesananClick = { navController.navigate(Screen.SellerOrders.route) },
                    onNavItemClick = onSellerBottomNav
                )
            }

            composable(Screen.AddCatch.route) {
                AddCatchScreen(
                    onSaveSuccess = {
                        navController.navigate(Screen.Inventory.route) {
                            popUpTo(Screen.SellerDashboard.route) { inclusive = false }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Inventory.route) {
                InventoryScreen(
                    onProductClick = { product ->
                        selectedSellerProduct = product
                        navController.navigate(Screen.ProductDetail.createRoute(product.id))
                    },
                    onAddCatchClick = { navController.navigate(Screen.AddCatch.route) },
                    onNavItemClick = onSellerBottomNav
                )
            }

            composable(
                route = Screen.ProductDetail.route,
                arguments = listOf(
                    navArgument(Screen.ProductDetail.ARG_PRODUCT_ID) {
                        type = NavType.StringType
                    }
                )
            ) {
                ProductDetailScreen(
                    product = selectedSellerProduct,
                    onBack = { navController.popBackStack() },
                    onUpdatePriceClick = { product ->
                        selectedSellerProduct = product
                        navController.navigate(Screen.UpdatePrice.createRoute(product.id))
                    }
                )
            }

            composable(
                route = Screen.UpdatePrice.route,
                arguments = listOf(
                    navArgument(Screen.UpdatePrice.ARG_PRODUCT_ID) {
                        type = NavType.StringType
                    }
                )
            ) {
                UpdatePriceScreen(
                    product = selectedSellerProduct,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.SellerOrders.route) {
                SellerOrderListScreen(
                    onBack = { navController.popBackStack() },
                    onOrderClick = { order ->
                        navController.navigate(Screen.SellerOrderDetail.createRoute(order.id))
                    }
                )
            }

            composable(
                route = Screen.SellerOrderDetail.route,
                arguments = listOf(
                    navArgument(Screen.SellerOrderDetail.ARG_ORDER_ID) {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString(Screen.SellerOrderDetail.ARG_ORDER_ID)
                val order = SellerOrderUiState.sampleSellerOrders()
                    .firstOrNull { it.id == orderId }
                    ?: SellerOrderUiState.sampleSellerOrders().first()
                SellerOrderDetailScreen(
                    initialOrder = order,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.SellerProfile.route) {
                ProfileScreen(
                    userName = sessionUser.name,
                    userEmail = sessionUser.email,
                    userRole = UserRole.PENJUAL,
                    onNavItemClick = onSellerBottomNav,
                    onLogout = logoutToAuth
                )
            }
        }

        navigation(
            startDestination = Screen.AdminDashboard.route,
            route = Screen.AdminGraph.route
        ) {
            composable(Screen.AdminDashboard.route) {
                AdminDashboardScreen(
                    onManageDestinations = { navController.navigate(Screen.ManageDestinations.route) },
                    onManageActivities = { navController.navigate(Screen.ManageDestinations.route) },
                    onManageUsers = { navController.navigate(Screen.ManageUsers.route) },
                    onNavItemClick = onAdminBottomNav
                )
            }

            composable(Screen.ManageDestinations.route) {
                ManageDestinationScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ManageUsers.route) {
                ManageUsersScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AdminProfile.route) {
                ProfileScreen(
                    userName = sessionUser.name,
                    userEmail = sessionUser.email,
                    userRole = UserRole.ADMIN,
                    onNavItemClick = onAdminBottomNav,
                    onLogout = logoutToAuth
                )
            }
        }
    }
}
