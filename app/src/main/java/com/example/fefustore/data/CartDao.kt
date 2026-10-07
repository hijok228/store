package com.example.fefustore.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Query("SELECT * FROM cart_items")
    fun getAllItems(): Flow<List<CartEntity>>

    @Query("""
        SELECT * FROM cart_items
        WHERE productId = :productId AND sizeId = :sizeId
        LIMIT 1
    """)
    suspend fun getItem(
        productId: String,
        sizeId: String
    ): CartEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CartEntity)

    @Query("""
        UPDATE cart_items
        SET quantity = quantity + 1
        WHERE productId = :productId AND sizeId = :sizeId
    """)
    suspend fun increaseQuantity(
        productId: String,
        sizeId: String
    )

    @Query("""
        UPDATE cart_items
        SET quantity = quantity - 1
        WHERE productId = :productId AND sizeId = :sizeId
    """)
    suspend fun decreaseQuantity(
        productId: String,
        sizeId: String
    )

    @Query("""
        DELETE FROM cart_items
        WHERE productId = :productId AND sizeId = :sizeId
    """)
    suspend fun deleteItem(
        productId: String,
        sizeId: String
    )

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}