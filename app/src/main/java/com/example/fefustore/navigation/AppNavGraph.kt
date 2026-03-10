package com.example.fefustore.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fefustore.ui.catalog.CatalogScreen
import com.example.fefustore.ui.detail.ProductDetailScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "catalog"
    ) {
        composable("catalog") {
            CatalogScreen(
                onProductClick = { productId ->
                    navController.navigate("detail/$productId")
                }
            )
        }

        composable("detail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments
                ?.getString("productId")
                ?.toIntOrNull() ?: 0

            ProductDetailScreen(
                productId = productId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}