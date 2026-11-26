package com.fishflow.app.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.fishflow.app.R
import com.fishflow.app.core.ui.components.ErrorToastEffect
import com.fishflow.app.data.model.FishProduct
import com.fishflow.app.data.model.Order
import com.fishflow.app.data.model.OrderItem
import com.fishflow.app.data.model.OrderStatus
import com.fishflow.app.data.model.ProductStatus
import com.fishflow.app.data.model.UserRole
import com.fishflow.app.data.repository.AuthRepositoryImpl
import com.fishflow.app.feature.admin.ActivityFormScreen
import com.fishflow.app.feature.admin.ActivityFormState
import com.fishflow.app.feature.admin.AdminDashboardScreen
import com.fishflow.app.feature.admin.DestinationFormScreen
import com.fishflow.app.feature.admin.DestinationFormState
import com.fishflow.app.feature.admin.ManageDestinationScreen
import com.fishflow.app.feature.admin.ManageDestinationViewModel
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
import com.fishflow.app.feature.seller.orders.SellerOrderDetailViewModel
import com.fishflow.app.feature.seller.orders.SellerOrderListScreen
import com.fishflow.app.feature.seller.orders.SellerOrderListViewModel
import com.fishflow.app.feature.seller.orders.SellerOrderUiState
import com.fishflow.app.feature.tourist.budget.BudgetPlannerScreen
import com.fishflow.app.feature.tourist.cart.CartItem
import com.fishflow.app.feature.tourist.cart.CartScreen
import com.fishflow.app.feature.tourist.cart.CartUiState
import com.fishflow.app.feature.tourist.cart.CartViewModel
import com.fishflow.app.feature.tourist.catalog.FishCatalogScreen
import com.fishflow.app.feature.tourist.catalog.FishDetailScreen
import com.fishflow.app.feature.tourist.checkout.CheckoutScreen
import com.fishflow.app.feature.tourist.checkout.CheckoutUiState
import com.fishflow.app.feature.tourist.checkout.CheckoutViewModel
import com.fishflow.app.feature.tourist.destination.DestinationDetailScreen
import com.fishflow.app.feature.tourist.destination.DestinationListScreen
import com.fishflow.app.feature.tourist.home.TouristHomeScreen
import com.fishflow.app.feature.tourist.order_history.OrderHistoryScreen
import com.fishflow.app.feature.tourist.order_history.OrderHistoryViewModel
import com.fishflow.app.feature.tourist.order_history.OrderTrackingScreen
import com.fishflow.app.feature.tourist.order_history.OrderTrackingViewModel
import com.fishflow.app.feature.tourist.order_history.sampleTouristOrders
import com.fishflow.app.feature.tourist.order_history.sampleTrackingOrder
import kotlinx.coroutines.launch
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
    val authRepository = remember { AuthRepositoryImpl() }
    val coroutineScope = rememberCoroutineScope()
    val cartViewModel: CartViewModel = viewModel()
    val checkoutViewModel: CheckoutViewModel = viewModel()
    val orderHistoryViewModel: OrderHistoryViewModel = viewModel()
    val sellerOrderListViewModel: SellerOrderListViewModel = viewModel()

    var sessionUser by remember { mutableStateOf(SessionUser()) }
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
        cartViewModel.loadCart()
        orderHistoryViewModel.refresh()
        sellerOrderListViewModel.refresh()
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

    LaunchedEffect(Unit) {
        authRepository.getCurrentUser()?.let { user ->
            navigateAfterAuth(user.role, user.nama, user.email)
        }
    }

    val logoutToAuth: () -> Unit = {
        coroutineScope.launch {
            authRepository.logout()
        }
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
                        cartViewModel.onAddItem(product, quantity)
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
                val cartState by cartViewModel.uiState.collectAsStateWithLifecycle()
                CartScreen(
                    initialState = cartState,
                    onBack = { navController.popBackStack() },
                    onBrowseCatalog = {
                        navController.navigate(Screen.FishCatalog.createRoute())
                    },
                    onUpdateQuantity = cartViewModel::onUpdateQuantity,
                    onRemoveItem = cartViewModel::onRemoveItem,
                    onProceedToCheckout = { updatedCart ->
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
                        checkoutViewModel.setOrderItems(orderItems)
                        navController.navigate(Screen.Checkout.route)
                    }
                )
            }

            composable(Screen.Checkout.route) {
                val checkoutState by checkoutViewModel.uiState.collectAsStateWithLifecycle()
                ErrorToastEffect(
                    errorMessage = checkoutState.errorMessage,
                    onConsumed = checkoutViewModel::clearError
                )
                LaunchedEffect(checkoutState.isOrderCreated, checkoutState.orderId) {
                    val createdId = checkoutState.orderId
                    if (checkoutState.isOrderCreated && !createdId.isNullOrBlank()) {
                        checkoutViewModel.resetCreatedState()
                        cartViewModel.loadCart()
                        orderHistoryViewModel.refresh()
                        navController.navigate(Screen.OrderTracking.createRoute(createdId)) {
                            popUpTo(Screen.TouristHome.route) { inclusive = false }
                        }
                    }
                }
                CheckoutScreen(
                    initialState = checkoutState,
                    onBack = { navController.popBackStack() },
                    onConfirmOrder = {
                        checkoutViewModel.onConfirmOrder()
                    }
                )
            }

            composable(Screen.OrderHistory.route) {
                val historyState by orderHistoryViewModel.uiState.collectAsStateWithLifecycle()
                ErrorToastEffect(
                    errorMessage = historyState.errorMessage,
                    onConsumed = orderHistoryViewModel::clearError
                )
                OrderHistoryScreen(
                    orders = historyState.orders,
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
                val orderId = backStackEntry.arguments?.getString(Screen.OrderTracking.ARG_ORDER_ID).orEmpty()
                val trackingViewModel: OrderTrackingViewModel = viewModel()
                val trackingState by trackingViewModel.uiState.collectAsStateWithLifecycle()
                ErrorToastEffect(
                    errorMessage = trackingState.errorMessage,
                    onConsumed = trackingViewModel::clearError
                )
                LaunchedEffect(orderId) {
                    trackingViewModel.observeOrder(orderId)
                }
                val loadedOrder = trackingState.order
                if (loadedOrder == null && trackingState.isLoading) {
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        androidx.compose.material3.CircularProgressIndicator()
                    }
                } else if (loadedOrder != null) {
                    OrderTrackingScreen(
                        initialOrder = loadedOrder,
                        isRefreshing = trackingState.isLoading,
                        onRefresh = { trackingViewModel.observeOrder(orderId) },
                        onCancelOrder = trackingViewModel::cancelOrder,
                        onBack = { navController.popBackStack() }
                    )
                }
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
                val sellerOrderState by sellerOrderListViewModel.uiState.collectAsStateWithLifecycle()
                ErrorToastEffect(
                    errorMessage = sellerOrderState.errorMessage,
                    onConsumed = sellerOrderListViewModel::clearError
                )
                SellerOrderListScreen(
                    initialState = sellerOrderState,
                    onBack = { navController.popBackStack() },
                    onOrderClick = { order ->
                        navController.navigate(Screen.SellerOrderDetail.createRoute(order.id))
                    },
                    onConfirmOrder = sellerOrderListViewModel::confirmOrder,
                    onRejectOrder = sellerOrderListViewModel::rejectOrder,
                    onCompleteOrder = sellerOrderListViewModel::completeOrder
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
                val orderId = backStackEntry.arguments?.getString(Screen.SellerOrderDetail.ARG_ORDER_ID).orEmpty()
                val detailViewModel: SellerOrderDetailViewModel = viewModel()
                val detailState by detailViewModel.uiState.collectAsStateWithLifecycle()
                ErrorToastEffect(
                    errorMessage = detailState.errorMessage,
                    onConsumed = detailViewModel::clearError
                )
                LaunchedEffect(orderId) {
                    detailViewModel.observeOrder(orderId)
                }
                val detailOrder = detailState.order
                if (detailOrder == null && detailState.isLoading) {
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        androidx.compose.material3.CircularProgressIndicator()
                    }
                } else if (detailOrder != null) {
                    SellerOrderDetailScreen(
                        initialOrder = detailOrder,
                        onBack = { navController.popBackStack() },
                        onConfirmOrder = detailViewModel::onConfirmOrder,
                        onRejectOrder = detailViewModel::onRejectOrder,
                        onCompleteOrder = detailViewModel::onCompleteOrder
                    )
                }
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

            composable(Screen.ManageDestinations.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Screen.AdminGraph.route)
                }
                val manageViewModel: ManageDestinationViewModel = viewModel(
                    viewModelStoreOwner = parentEntry,
                    factory = ManageDestinationViewModel.factory()
                )
                ManageDestinationScreen(
                    onBack = { navController.popBackStack() },
                    onOpenDestinationForm = {
                        navController.navigate(Screen.DestinationForm.route)
                    },
                    onOpenActivityForm = {
                        navController.navigate(Screen.ActivityForm.route)
                    },
                    viewModel = manageViewModel
                )
            }

            composable(Screen.DestinationForm.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Screen.AdminGraph.route)
                }
                val manageViewModel: ManageDestinationViewModel = viewModel(
                    viewModelStoreOwner = parentEntry,
                    factory = ManageDestinationViewModel.factory()
                )
                val uiState by manageViewModel.uiState.collectAsStateWithLifecycle()
                val form = uiState.destinationForm

                LaunchedEffect(form) {
                    if (form == null) {
                        navController.popBackStack()
                    }
                }

                DestinationFormScreen(
                    form = form ?: DestinationFormState(),
                    onFormChange = manageViewModel::onDestinationFormChange,
                    onSave = manageViewModel::onSaveDestination,
                    onCancel = {
                        manageViewModel.onDismissDestinationForm()
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.ActivityForm.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Screen.AdminGraph.route)
                }
                val manageViewModel: ManageDestinationViewModel = viewModel(
                    viewModelStoreOwner = parentEntry,
                    factory = ManageDestinationViewModel.factory()
                )
                val uiState by manageViewModel.uiState.collectAsStateWithLifecycle()
                val form = uiState.activityForm

                LaunchedEffect(form) {
                    if (form == null) {
                        navController.popBackStack()
                    }
                }

                ActivityFormScreen(
                    form = form ?: ActivityFormState(),
                    onFormChange = manageViewModel::onActivityFormChange,
                    onSave = manageViewModel::onSaveActivity,
                    onCancel = {
                        manageViewModel.onDismissActivityForm()
                        navController.popBackStack()
                    }
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
