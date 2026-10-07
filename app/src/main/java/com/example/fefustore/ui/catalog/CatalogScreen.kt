package com.example.fefustore.ui.catalog

import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info

import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel

import coil3.compose.AsyncImage

import com.example.fefustore.data.CartRepository
import com.example.fefustore.data.ProductRepository
import com.example.fefustore.model.Product
import com.example.fefustore.viewmodel.CatalogViewModel
import com.example.fefustore.viewmodel.CatalogViewModelFactory

import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen() {

    val context = LocalContext.current

    val viewModel: CatalogViewModel = viewModel(
        factory = CatalogViewModelFactory(
            ProductRepository(context)
        )
    )

    val uiState by viewModel.uiState.collectAsState()

    val cartRepository = remember {
        CartRepository(context)
    }

    val coroutineScope = rememberCoroutineScope()

    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    var selectedProduct by remember {
        mutableStateOf<Product?>(null)
    }

    var selectedSize by remember {
        mutableStateOf<String?>(null)
    }

    var showInfo by remember {
        mutableStateOf(false)
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    /*
     * Показываем сообщение, если API недоступен,
     * но каталог уже есть в Room.
     */
    LaunchedEffect(uiState.isOffline) {
        if (uiState.isOffline) {
            snackbarHostState.showSnackbar("Нет сети")
            viewModel.clearOfflineMessage()
        }
    }

    val tabs = listOf("Новинки") +
            uiState.categories.map { it.name }

    val filteredProducts = when (selectedTab) {

        0 -> {
            uiState.products.filter { product ->
                product.tags.any {
                    it.equals("New", ignoreCase = true)
                }
            }
        }

        else -> {
            val categoryIndex = selectedTab - 1

            if (categoryIndex in uiState.categories.indices) {

                val categoryId =
                    uiState.categories[categoryIndex].id

                uiState.products.filter {
                    it.categoryId == categoryId
                }

            } else {
                emptyList()
            }
        }
    }

    Scaffold(
        topBar = {
            Text(
                text = "Каталог товаров",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(
                    start = 16.dp,
                    top = 16.dp,
                    bottom = 8.dp
                )
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            /*
             * Категории
             */
            if (tabs.isNotEmpty()) {

                ScrollableTabRow(
                    selectedTabIndex = selectedTab
                ) {

                    tabs.forEachIndexed { index, title ->

                        Tab(
                            selected = selectedTab == index,
                            onClick = {
                                selectedTab = index
                            },
                            text = {
                                Text(title)
                            }
                        )
                    }
                }
            }

            /*
             * Состояния загрузки / ошибки / каталога
             */
            when {

                uiState.isLoading -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        CircularProgressIndicator()
                    }
                }

                uiState.errorMessage != null -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = uiState.errorMessage
                                    ?: "Ошибка"
                            )

                            Button(
                                onClick = {
                                    viewModel.loadCatalog()
                                },
                                modifier = Modifier.padding(top = 12.dp)
                            ) {
                                Text("Повторить")
                            }
                        }
                    }
                }

                else -> {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        items(
                            items = filteredProducts,
                            key = { it.id }
                        ) { product ->

                            ProductCard(
                                product = product,
                                onClick = {

                                    selectedProduct = product
                                    selectedSize = null
                                    showInfo = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    /*
     * Bottom Sheet товара
     */
    selectedProduct?.let { product ->

        ModalBottomSheet(
            onDismissRequest = {

                selectedProduct = null
                showInfo = false
            }
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 24.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                /*
                 * Заголовок
                 */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = {
                            selectedProduct = null
                        }
                    ) {

                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Закрыть"
                        )
                    }
                }

                /*
                 * Изображение + теги
                 */
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                ) {

                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(
                                RoundedCornerShape(16.dp)
                            ),
                        contentScale = ContentScale.Crop
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {

                        product.tags.forEach { tag ->

                            AssistChip(
                                onClick = {},
                                label = {
                                    Text(tag)
                                }
                            )
                        }
                    }
                }

                /*
                 * Цена
                 */
                Text(
                    text = product.price,
                    style = MaterialTheme.typography.headlineSmall
                )

                /*
                 * Описание
                 */
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyLarge
                )

                /*
                 * Размеры
                 */
                if (product.sizes.isNotEmpty()) {

                    Text(
                        text = "Размер",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        product.sizes.forEach { size ->

                            FilterChip(
                                selected = selectedSize == size.name,
                                onClick = {
                                    selectedSize = size.name
                                },
                                label = {
                                    Text(size.name)
                                }
                            )
                        }
                    }
                }

                /*
                 * Информация о товаре
                 */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )

                    TextButton(
                        onClick = {
                            showInfo = !showInfo
                        }
                    ) {

                        Text("Информация о товаре")
                    }
                }

                if (showInfo) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {

                        Text(
                            text = "Материал: ${product.material}"
                        )

                        Text(
                            text = "Вес: ${product.weight}"
                        )

                        Text(
                            text = "Сезон: ${product.season}"
                        )

                        Text(
                            text = "Страна производства: ${product.countryOfOrigin}"
                        )
                    }
                }

                /*
                 * Добавление в корзину
                 *
                 * В Room сохраняются:
                 * productId
                 * sizeId
                 * quantity
                 */
                Button(
                    onClick = {

                        selectedSize?.let { sizeName ->

                            val size = product.sizes.find {
                                it.name == sizeName
                            }

                            if (size != null) {

                                coroutineScope.launch {

                                    cartRepository.addProduct(
                                        productId = product.id,
                                        sizeId = size.id
                                    )
                                }

                                selectedProduct = null
                            }
                        }
                    },
                    enabled = selectedSize != null,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text =
                            if (selectedSize != null) {
                                "В корзину — $selectedSize"
                            } else {
                                "Выберите размер"
                            }
                    )
                }

                /*
                 * Закрыть
                 */
                TextButton(
                    onClick = {
                        selectedProduct = null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text("Закрыть")
                }
            }
        }
    }
}