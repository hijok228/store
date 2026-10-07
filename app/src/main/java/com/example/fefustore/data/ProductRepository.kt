package com.example.fefustore.data

import com.example.fefustore.model.CatalogData
import android.content.Context
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.io.IOException
import retrofit2.HttpException
class ProductRepository(
    context: Context
) {

    private val database = AppDatabase.getInstance(context)
    private val productDao = database.productDao()
    private val categoryDao = database.categoryDao()
    private val api = RetrofitClient.api

    companion object {
        private const val AUTHORIZATION =
            "Bearer Cmt7wdwFgDIi1_SRX8hlJIExs0jJKPr4axflLpExAxM"
    }

    /**
     * Наблюдает за данными из локальной базы Room.
     *
     * Когда API обновит базу, Flow автоматически передаст
     * новые данные в ViewModel.
     */
    fun observeCatalog(): Flow<CatalogData> {
        return combine(
            productDao.getAllProducts(),
            categoryDao.getAllCategories()
        ) { products, categories ->

            CatalogData(
                categories = categories.map { it.toCategory() },
                items = products.map { it.toProduct() }
            )
        }
    }

    /**
     * Проверяет, есть ли данные в локальном кэше.
     */
    suspend fun hasCache(): Boolean {
        val products = productDao.getAllProducts().first()
        val categories = categoryDao.getAllCategories().first()

        return products.isNotEmpty() || categories.isNotEmpty()
    }

    /**
     * Загружает каталог из API и сохраняет его в Room.
     */
    suspend fun refreshCatalog(): Result<Unit> {
        return try {
            val catalog = api.getCatalog(AUTHORIZATION)

            database.withTransaction {
                productDao.clearProducts()
                categoryDao.clearCategories()

                productDao.insertProducts(
                    catalog.items.map { it.toEntity() }
                )

                categoryDao.insertCategories(
                    catalog.categories.map { it.toEntity() }
                )
            }

            Result.success(Unit)

        } catch (e: HttpException) {
            println("API ERROR: HTTP ${e.code()} ${e.message()}")
            Result.failure(e)

        } catch (e: IOException) {
            println("API ERROR: Network error ${e.message}")
            Result.failure(e)

        } catch (e: Exception) {
            println("API ERROR: ${e.message}")
            Result.failure(e)
        }
    }
}