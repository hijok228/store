package com.example.fefustore

import com.example.fefustore.model.Product
import com.example.fefustore.model.ProductSize
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductTest {

    private fun createProduct(
        priceInKopecks: Int = 199900
    ): Product {
        return Product(
            id = "1",
            title = "Футболка",
            shortDescription = "Короткое описание",
            description = "Полное описание товара",
            priceInKopecks = priceInKopecks,
            imageUrl = "https://example.com/image.jpg",
            tags = listOf("New", "Popular"),
            sizes = listOf(
                ProductSize("xs", "XS"),
                ProductSize("m", "M"),
                ProductSize("xl", "XL")
            ),
            categoryId = "clothes",
            material = "Хлопок",
            weight = "200 г",
            season = "Лето",
            countryOfOrigin = "Россия"
        )
    }

    @Test
    fun price_is_converted_from_kopecks_to_rubles() {
        val product = createProduct(199900)

        assertEquals("1 999 ₽", product.price)
    }

    @Test
    fun price_formats_thousands_with_spaces() {
        val product = createProduct(12345600)

        assertEquals("123 456 ₽", product.price)
    }

    @Test
    fun zero_price_is_formatted_correctly() {
        val product = createProduct(0)

        assertEquals("0 ₽", product.price)
    }

    @Test
    fun product_contains_required_sizes() {
        val product = createProduct()

        assertTrue(product.sizes.any { it.name == "XS" })
        assertTrue(product.sizes.any { it.name == "M" })
        assertTrue(product.sizes.any { it.name == "XL" })
    }

    @Test
    fun product_contains_new_tag() {
        val product = createProduct()

        assertTrue(product.tags.contains("New"))
    }
}