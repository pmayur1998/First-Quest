package com.example.second_quest.domain.repository

import com.example.second_quest.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getLocalProducts(query: String): Flow<List<Product>>
    fun getRemoteProducts(query: String): Flow<List<Product>>
    suspend fun refreshProducts(needToRefreshDB: Boolean = false): List<Product>
}