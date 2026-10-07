package com.example.fefustore.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

class CartRepository(context: Context) {

    private val database = AppDatabase.getInstance(context)
    private val cartDao = database.cartDao()

    val items: Flow<List<CartEntity>> =
        cartDao.getAllItems()

    suspend fun addProduct(
        productId: String,
        sizeId: String
    ) {
        val existing = cartDao.getItem(
            productId = productId,
            sizeId = sizeId
        )

        if (existing == null) {
            cartDao.insertItem(
                CartEntity(
                    productId = productId,
                    sizeId = sizeId,
                    quantity = 1
                )
            )
        } else {
            cartDao.increaseQuantity(
                productId = productId,
                sizeId = sizeId
            )
        }
    }

    suspend fun increaseProduct(
        productId: String,
        sizeId: String
    ) {
        cartDao.increaseQuantity(
            productId = productId,
            sizeId = sizeId
        )
    }

    suspend fun decreaseProduct(
        productId: String,
        sizeId: String
    ) {
        val item = cartDao.getItem(
            productId = productId,
            sizeId = sizeId
        ) ?: return

        if (item.quantity > 1) {
            cartDao.decreaseQuantity(
                productId = productId,
                sizeId = sizeId
            )
        } else {
            cartDao.deleteItem(
                productId = productId,
                sizeId = sizeId
            )
        }
    }

    suspend fun removeProduct(
        productId: String,
        sizeId: String
    ) {
        cartDao.deleteItem(
            productId = productId,
            sizeId = sizeId
        )
    }

    suspend fun clear() {
        cartDao.clearCart()
    }
}