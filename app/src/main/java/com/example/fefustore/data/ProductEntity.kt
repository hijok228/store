package com.example.fefustore.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(

    @PrimaryKey
    val id: String,

    val name: String,

    val shortDescription: String,

    val longDescription: String,

    val priceInKopecks: Int,

    val imageUrl: String,

    val tagsJson: String,

    val sizesJson: String,

    val categoryId: String,

    val material: String,

    val weight: String,

    val season: String,

    val countryOfOrigin: String
)