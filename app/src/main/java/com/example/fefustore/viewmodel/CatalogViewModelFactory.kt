package com.example.fefustore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fefustore.data.ProductRepository

class CatalogViewModelFactory(
    private val repository: ProductRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(CatalogViewModel::class.java)) {
            return CatalogViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}