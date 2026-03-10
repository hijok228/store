package com.example.fefustore.data

import com.example.fefustore.model.Product

object ProductRepository {
    val products = listOf(
        Product(
            id = 1,
            title = "Рюкзак Fjallraven",
            price = "8 990 ₽",
            description = "Удобный городской рюкзак для учёбы, прогулок и поездок.",
            imageUrl = "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg"
        ),
        Product(
            id = 2,
            title = "Мужская футболка",
            price = "1 590 ₽",
            description = "Базовая футболка из хлопка на каждый день.",
            imageUrl = "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879_.jpg"
        ),
        Product(
            id = 3,
            title = "Мужская куртка",
            price = "6 490 ₽",
            description = "Лёгкая и удобная куртка для прохладной погоды.",
            imageUrl = "https://fakestoreapi.com/img/71li-ujtlUL._AC_UX679_.jpg"
        ),
        Product(
            id = 4,
            title = "Женская куртка",
            price = "7 290 ₽",
            description = "Стильная женская куртка с современным дизайном.",
            imageUrl = "https://fakestoreapi.com/img/71YXzeOuslL._AC_UY879_.jpg"
        ),
        Product(
            id = 5,
            title = "Браслет",
            price = "12 990 ₽",
            description = "Элегантный аксессуар, который подойдёт на каждый день.",
            imageUrl = "https://fakestoreapi.com/img/71pWzhdJNwL._AC_UL640_QL65_ML3_.jpg"
        ),
        Product(
            id = 6,
            title = "Игровой SSD",
            price = "14 990 ₽",
            description = "Накопитель для быстрой загрузки игр и программ.",
            imageUrl = "https://fakestoreapi.com/img/61U7T1koQqL._AC_SX679_.jpg"
        )
    )

    fun getProductById(id: Int): Product? {
        return products.find { it.id == id }
    }
}