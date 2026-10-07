package com.example.fefustore.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fefustore.data.CartRepository
import com.example.fefustore.ui.cart.CartScreen
import com.example.fefustore.ui.catalog.CatalogScreen

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()

    val context = LocalContext.current

    val cartRepository = remember {
        CartRepository(context)
    }

    /*
     * Получаем корзину непосредственно из Room.
     */
    val cartItems by cartRepository.items.collectAsState(
        initial = emptyList()
    )

    /*
     * Считаем общее количество товаров.
     *
     * Например:
     * футболка × 2
     * джинсы × 1
     *
     * Бейдж = 3
     */
    val cartCount = cartItems.sumOf {
        it.quantity
    }

    val navBackStackEntry by navController
        .currentBackStackEntryAsState()

    val currentRoute =
        navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {

            NavigationBar {

                /*
                 * Каталог
                 */
                NavigationBarItem(
                    selected = currentRoute == "catalog",

                    onClick = {
                        navController.navigate("catalog") {
                            popUpTo("catalog") {
                                inclusive = false
                            }

                            launchSingleTop = true
                        }
                    },

                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Каталог"
                        )
                    },

                    label = {
                        Text("Каталог")
                    }
                )

                /*
                 * Корзина
                 */
                NavigationBarItem(
                    selected = currentRoute == "cart",

                    onClick = {
                        navController.navigate("cart") {
                            launchSingleTop = true
                        }
                    },

                    icon = {

                        if (cartCount > 0) {

                            BadgedBox(
                                badge = {
                                    Badge {
                                        Text(
                                            text = cartCount.toString()
                                        )
                                    }
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.ShoppingCart,
                                    contentDescription =
                                        "Корзина"
                                )
                            }

                        } else {

                            Icon(
                                imageVector =
                                    Icons.Default.ShoppingCart,
                                contentDescription =
                                    "Корзина"
                            )
                        }
                    },

                    label = {
                        Text("Корзина")
                    }
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "catalog",
            modifier = Modifier.padding(innerPadding)
        ) {

            composable("catalog") {
                CatalogScreen()
            }

            composable("cart") {
                CartScreen()
            }
        }
    }
}