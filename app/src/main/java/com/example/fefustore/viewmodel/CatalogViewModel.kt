package com.example.fefustore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fefustore.data.ProductRepository
import com.example.fefustore.model.Category
import com.example.fefustore.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CatalogUiState(
    val products: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedTab: Int = 0,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isOffline: Boolean = false
)

class CatalogViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    init {
        loadCatalog()
    }

    fun loadCatalog() {

        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            isOffline = false
        )

        // Сначала наблюдаем локальный кэш Room
        viewModelScope.launch {

            repository.observeCatalog().collect { catalog ->

                if (catalog.items.isNotEmpty() || catalog.categories.isNotEmpty()) {

                    _uiState.value = _uiState.value.copy(
                        products = catalog.items,
                        categories = catalog.categories,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
        }

        // Одновременно пытаемся получить свежие данные из API
        viewModelScope.launch {

            val result = repository.refreshCatalog()

            if (result.isFailure) {

                val hasCache = repository.hasCache()

                if (hasCache) {

                    // Есть кэш → продолжаем показывать его
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isOffline = true,
                        errorMessage = null
                    )

                } else {

                    // Нет ни интернета, ни кэша
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Не удалось загрузить товары",
                        isOffline = false
                    )
                }
            }
        }
    }

    fun selectTab(tab: Int) {
        _uiState.value = _uiState.value.copy(
            selectedTab = tab
        )
    }

    fun clearOfflineMessage() {
        _uiState.value = _uiState.value.copy(
            isOffline = false
        )
    }
}