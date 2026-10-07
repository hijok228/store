package com.example.fefustore.data

import com.example.fefustore.model.CatalogData
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {

    @GET("catalog")
    suspend fun getCatalog(
        @Header("Authorization")
        authorization: String
    ): CatalogData
}