package com.example.fefustore.ui.cart

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.fefustore.data.CartEntity
import com.example.fefustore.data.CartRepository
import com.example.fefustore.data.ProductRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen() {

    val context = LocalContext.current

    val cartRepository = remember {
        CartRepository(context)
    }

    val productRepository = remember {
        ProductRepository(context)
    }

    val coroutineScope = rememberCoroutineScope()

    val cartItems by cartRepository.items.collectAsState(
        initial = emptyList()
    )

    val catalog by productRepository.observeCatalog().collectAsState(
        initial = null
    )

    var showClearDialog by remember {
        mutableStateOf(false)
    }

    var showOrderSheet by remember {
        mutableStateOf(false)
    }

    var showSuccessDialog by remember {
        mutableStateOf(false)
    }

    var name by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var comment by remember {
        mutableStateOf("")
    }

    /*
     * Проверяем email.
     * Это простая проверка структуры адреса:
     * example@mail.com
     */
    val emailIsValid = email.matches(
        Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    )

    val canOrder =
        name.isNotBlank() && emailIsValid

    /*
     * Сопоставляем записи корзины из Room
     * с товарами из каталога.
     *
     * В Room хранятся только:
     * productId
     * sizeId
     * quantity
     */
    val displayItems = remember(
        cartItems,
        catalog
    ) {
        val products = catalog?.items ?: emptyList()

        cartItems.mapNotNull { cartItem ->

            val product = products.find {
                it.id == cartItem.productId
            }

            if (product != null) {
                CartDisplayItem(
                    cartItem = cartItem,
                    productName = product.title,
                    priceInKopecks = product.priceInKopecks,
                    imageUrl = product.imageUrl,
                    sizeName = product.sizes
                        .find { size ->
                            size.id == cartItem.sizeId
                        }
                        ?.name
                        ?: cartItem.sizeId
                )
            } else {
                null
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Корзина",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        /*
         * Пустая корзина
         */
        if (displayItems.isEmpty()) {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "Корзина пуста",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Добавьте товары из каталога"
                )
            }

            return@Column
        }

        /*
         * Список товаров
         */
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(
                items = displayItems,
                key = {
                    "${it.cartItem.productId}_${it.cartItem.sizeId}"
                }
            ) { item ->

                CartItemCard(
                    item = item,

                    onIncrease = {
                        coroutineScope.launch {
                            cartRepository.increaseProduct(
                                productId = item.cartItem.productId,
                                sizeId = item.cartItem.sizeId
                            )
                        }
                    },

                    onDecrease = {
                        coroutineScope.launch {
                            cartRepository.decreaseProduct(
                                productId = item.cartItem.productId,
                                sizeId = item.cartItem.sizeId
                            )
                        }
                    },

                    onDelete = {
                        coroutineScope.launch {
                            cartRepository.removeProduct(
                                productId = item.cartItem.productId,
                                sizeId = item.cartItem.sizeId
                            )
                        }
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        HorizontalDivider()

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        /*
         * Общая сумма
         */
        val totalKopecks = displayItems.sumOf {
            it.priceInKopecks * it.cartItem.quantity
        }

        Text(
            text = "Итого: %,d ₽".format(
                totalKopecks / 100
            ).replace(',', ' '),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        /*
         * Кнопка оформления заказа
         */
        Button(
            onClick = {
                showOrderSheet = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Оформить")
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        /*
         * Очистить корзину
         */
        OutlinedButton(
            onClick = {
                showClearDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Очистить корзину")
        }
    }

    /*
     * Диалог подтверждения очистки
     */
    if (showClearDialog) {

        AlertDialog(
            onDismissRequest = {
                showClearDialog = false
            },

            title = {
                Text("Очистить корзину?")
            },

            text = {
                Text(
                    "Все товары будут удалены из корзины."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        coroutineScope.launch {
                            cartRepository.clear()
                        }

                        showClearDialog = false
                    }
                ) {
                    Text("Очистить")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showClearDialog = false
                    }
                ) {
                    Text("Отмена")
                }
            }
        )
    }

    /*
     * Оформление заказа
     */
    if (showOrderSheet) {

        ModalBottomSheet(
            onDismissRequest = {
                showOrderSheet = false
            }
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 32.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Оформление заказа",
                    style = MaterialTheme.typography.headlineSmall
                )

                /*
                 * Имя
                 */
                OutlinedTextField(
                    value = name,

                    onValueChange = {
                        name = it
                    },

                    label = {
                        Text("Имя")
                    },

                    singleLine = true,

                    modifier = Modifier.fillMaxWidth(),

                    isError = name.isEmpty()
                )

                /*
                 * Email
                 */
                OutlinedTextField(
                    value = email,

                    onValueChange = {
                        email = it
                    },

                    label = {
                        Text("Email")
                    },

                    singleLine = true,

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),

                    modifier = Modifier.fillMaxWidth(),

                    isError = email.isNotEmpty() && !emailIsValid
                )

                /*
                 * Комментарий
                 */
                OutlinedTextField(
                    value = comment,

                    onValueChange = {
                        comment = it
                    },

                    label = {
                        Text("Комментарий")
                    },

                    modifier = Modifier.fillMaxWidth(),

                    minLines = 3
                )

                /*
                 * Кнопка подтверждения
                 */
                Button(
                    onClick = {

                        coroutineScope.launch {
                            cartRepository.clear()
                        }

                        showOrderSheet = false
                        showSuccessDialog = true

                        name = ""
                        email = ""
                        comment = ""
                    },

                    enabled = canOrder,

                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Оформить")
                }
            }
        }
    }

    /*
     * Сообщение после оформления
     */
    if (showSuccessDialog) {

        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
            },

            title = {
                Text("Заказ оформлен")
            },

            text = {
                Text(
                    "Спасибо! Ваш заказ успешно оформлен."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        showSuccessDialog = false
                    }
                ) {
                    Text("ОК")
                }
            }
        )
    }
}


/*
 * Данные товара, необходимые для отображения
 * записи из Room.
 */
private data class CartDisplayItem(
    val cartItem: CartEntity,
    val productName: String,
    val priceInKopecks: Int,
    val imageUrl: String,
    val sizeName: String
)


/*
 * Карточка товара в корзине
 */
@Composable
private fun CartItemCard(
    item: CartDisplayItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit
) {

    val lineTotal =
        item.priceInKopecks * item.cartItem.quantity

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            /*
             * Изображение товара
             */
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.productName,
                modifier = Modifier
                    .size(90.dp)
                    .clip(
                        RoundedCornerShape(8.dp)
                    ),
                contentScale = ContentScale.Crop
            )

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                /*
                 * Название
                 */
                Text(
                    text = item.productName,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                /*
                 * Размер
                 */
                Text(
                    text = "Размер: ${item.sizeName}"
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                /*
                 * Цена за один товар
                 */
                Text(
                    text = "Цена: %,d ₽".format(
                        item.priceInKopecks / 100
                    ).replace(',', ' ')
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                /*
                 * Итоговая цена позиции
                 */
                Text(
                    text = "Сумма: %,d ₽".format(
                        lineTotal / 100
                    ).replace(',', ' '),

                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                /*
                 * Управление количеством
                 */
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    OutlinedButton(
                        onClick = onDecrease,
                        modifier = Modifier
                            .width(48.dp)
                            .height(40.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Text("−")
                    }

                    Text(
                        text = item.cartItem.quantity.toString(),
                        modifier = Modifier.padding(
                            horizontal = 16.dp
                        ),
                        style = MaterialTheme.typography.titleMedium
                    )

                    OutlinedButton(
                        onClick = onIncrease,
                        modifier = Modifier
                            .width(48.dp)
                            .height(40.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Text("+")
                    }
                }

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                /*
                 * Удалить позицию
                 */
                TextButton(
                    onClick = onDelete
                ) {
                    Text("Удалить")
                }
            }
        }
    }
}