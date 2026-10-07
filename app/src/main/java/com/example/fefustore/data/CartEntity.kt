package com.example.fefustore.data

import androidx.room.Entity

@Entity(
    tableName = "cart_items",
    primaryKeys = ["productId", "sizeId"]
)
data class CartEntity(
    val productId: String,
    val sizeId: String,
    val quantity: Int
)