package com.example.fefustore.model

import com.google.gson.annotations.SerializedName

data class Product(
    val id: String,

    @SerializedName("name")
    val title: String,

    val shortDescription: String,

    @SerializedName("longDescription")
    val description: String,

    val priceInKopecks: Int,

    val imageUrl: String,

    val tags: List<String>,

    val sizes: List<ProductSize>,

    val categoryId: String,

    val material: String,

    val weight: String,

    val season: String,

    val countryOfOrigin: String
) {
    val price: String
        get() = "%,d ₽".format(priceInKopecks / 100).replace(',', ' ')
}

data class ProductSize(
    val id: String,
    val name: String
)

data class CatalogData(
    val categories: List<Category>,
    val items: List<Product>
)

data class Category(
    val id: String,
    val name: String
)