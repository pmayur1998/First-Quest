package com.example.second_quest.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface ProductApiService {
    @GET("products/search")
    suspend fun searchProducts(@Query("q") query: String): ProductsResponse

    @GET("products")
    suspend fun getAllProducts(): ProductsResponse
}