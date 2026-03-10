package com.example.fefustore.data

import com.example.fefustore.model.Product

object ProductRepository {
    val products = listOf(
        Product(
            id = 1,
            title = "Рюкзак Fjallraven",
            price = "6 600 ₽",
            description = "Удобный городской рюкзак для учёбы, прогулок и поездок.",
            imageUrl = "https://kanken-shop.net/wp-content/uploads/2019/11/kanken-classic-black-picture.jpg"
        ),
        Product(
            id = 2,
            title = "Мужская футболка",
            price = "580 ₽",
            description = "Базовая футболка из хлопка на каждый день.",
            imageUrl = "https://ditex.su/init/static/products/02/10/img500.jpg"
        ),
        Product(
            id = 3,
            title = "Мужская куртка",
            price = "6 490 ₽",
            description = "Лёгкая и удобная куртка для прохладной погоды.",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTwZFfwbjMYoQaI6Fl0oxnn3Xk_sqcfvOLuLA&s"
        ),
        Product(
            id = 4,
            title = "Женская куртка",
            price = "18 990 ₽",
            description = "Стильная женская куртка с современным дизайном.",
            imageUrl = "https://mongolshop.ru/upload/iblock/5f1/7m8g7at1700n7hyp3b69rz0sn030bdba/20-09-2476019.jpg"
        ),
        Product(
            id = 5,
            title = "Браслет",
            price = "12 990 ₽",
            description = "Элегантный аксессуар, который подойдёт на каждый день.",
            imageUrl = "https://optim.tildacdn.com/stor6162-3761-4136-b334-393361313762/-/format/webp/47555204.jpg.webp"
        ),
        Product(
            id = 6,
            title = "Игровой SSD",
            price = "19 900 ₽",
            description = "Накопитель для быстрой загрузки игр и программ.",
            imageUrl = "https://c.dns-shop.ru/thumb/st4/fit/500/500/b60c1ec8a195fc6bd4367a71e2f72533/c0e0fdbb70b14fa69e223b44b1ea251d48ea8fde682cf2a535075882a6c7a1aa.jpg.webp"
        )
    )

    fun getProductById(id: Int): Product? {
        return products.find { it.id == id }
    }
}