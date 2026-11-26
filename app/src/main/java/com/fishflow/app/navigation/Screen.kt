package com.fishflow.app.navigation

sealed class Screen(val route: String) {
    data object AuthGraph : Screen("auth_graph")
    data object Login : Screen("login")
    data object Register : Screen("register")

    data object TouristGraph : Screen("tourist_graph")
    data object TouristHome : Screen("tourist_home")
    data object FishCatalog : Screen("fish_catalog?query={query}") {
        const val ARG_QUERY = "query"
        fun createRoute(query: String? = null): String {
            return if (query.isNullOrBlank()) {
                "fish_catalog"
            } else {
                "fish_catalog?query=${java.net.URLEncoder.encode(query, "UTF-8")}"
            }
        }
    }
    data object FishDetail : Screen("fish_detail/{productId}") {
        const val ARG_PRODUCT_ID = "productId"
        fun createRoute(productId: String): String = "fish_detail/$productId"
    }
    data object Destinations : Screen("destinations")
    data object DestinationDetail : Screen("destination_detail/{destinationId}") {
        const val ARG_DESTINATION_ID = "destinationId"
        fun createRoute(destinationId: String): String = "destination_detail/$destinationId"
    }
    data object BudgetPlanner : Screen("budget_planner/{destinationId}/{destinationName}?selectedIds={selectedIds}") {
        const val ARG_DESTINATION_ID = "destinationId"
        const val ARG_DESTINATION_NAME = "destinationName"
        const val ARG_SELECTED_IDS = "selectedIds"
        fun createRoute(
            destinationId: String,
            destinationName: String,
            selectedIds: Set<String> = emptySet()
        ): String {
            val encodedName = java.net.URLEncoder.encode(destinationName, "UTF-8")
            val encodedIds = java.net.URLEncoder.encode(selectedIds.joinToString(","), "UTF-8")
            return "budget_planner/$destinationId/$encodedName?selectedIds=$encodedIds"
        }
    }
    data object Cart : Screen("cart")
    data object Checkout : Screen("checkout")
    data object OrderHistory : Screen("order_history")
    data object OrderTracking : Screen("order_tracking/{orderId}") {
        const val ARG_ORDER_ID = "orderId"
        fun createRoute(orderId: String): String = "order_tracking/$orderId"
    }
    data object TouristProfile : Screen("tourist_profile")

    data object SellerGraph : Screen("seller_graph")
    data object SellerDashboard : Screen("seller_dashboard")
    data object AddCatch : Screen("add_catch")
    data object Inventory : Screen("inventory")
    data object ProductDetail : Screen("product_detail/{productId}") {
        const val ARG_PRODUCT_ID = "productId"
        fun createRoute(productId: String): String = "product_detail/$productId"
    }
    data object UpdatePrice : Screen("update_price/{productId}") {
        const val ARG_PRODUCT_ID = "productId"
        fun createRoute(productId: String): String = "update_price/$productId"
    }
    data object SellerOrders : Screen("seller_orders")
    data object SellerOrderDetail : Screen("seller_order_detail/{orderId}") {
        const val ARG_ORDER_ID = "orderId"
        fun createRoute(orderId: String): String = "seller_order_detail/$orderId"
    }
    data object SellerProfile : Screen("seller_profile")

    data object AdminGraph : Screen("admin_graph")
    data object AdminDashboard : Screen("admin_dashboard")
    data object ManageDestinations : Screen("manage_destinations")
    data object ManageUsers : Screen("manage_users")
    data object AdminProfile : Screen("admin_profile")
}
