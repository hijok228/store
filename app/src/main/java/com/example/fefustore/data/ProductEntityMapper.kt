package com.example.fefustore.data

import com.example.fefustore.model.Category
import com.example.fefustore.model.Product
import com.example.fefustore.model.ProductSize
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private val gson = Gson()

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        name = title,
        shortDescription = shortDescription,
        longDescription = description,
        priceInKopecks = priceInKopecks,
        imageUrl = imageUrl,
        tagsJson = gson.toJson(tags),
        sizesJson = gson.toJson(sizes),
        categoryId = categoryId,
        material = material,
        weight = weight,
        season = season,
        countryOfOrigin = countryOfOrigin
    )
}

fun ProductEntity.toProduct(): Product {
    val sizesType = object : TypeToken<List<ProductSize>>() {}.type
    val tagsType = object : TypeToken<List<String>>() {}.type

    return Product(
        id = id,
        title = name,
        shortDescription = shortDescription,
        description = longDescription,
        priceInKopecks = priceInKopecks,
        imageUrl = imageUrl,
        tags = gson.fromJson(tagsJson, tagsType),
        sizes = gson.fromJson(sizesJson, sizesType),
        categoryId = categoryId,
        material = material,
        weight = weight,
        season = season,
        countryOfOrigin = countryOfOrigin
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name
    )
}

fun CategoryEntity.toCategory(): Category {
    return Category(
        id = id,
        name = name
    )
}