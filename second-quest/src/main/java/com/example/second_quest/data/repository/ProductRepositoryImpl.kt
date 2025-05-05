package com.example.second_quest.data.repository

import com.example.second_quest.data.api.ProductApiService
import com.example.second_quest.data.local.dao.ProductDao
import com.example.second_quest.data.mapper.toDomain
import com.example.second_quest.data.mapper.toEntity
import com.example.second_quest.domain.model.Product
import com.example.second_quest.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val apiService: ProductApiService, private val productDao: ProductDao
) : ProductRepository {
    override fun getRemoteProducts(query: String): Flow<List<Product>> = flow {
        val remoteProducts = apiService.searchProducts(query)
        productDao.insertProducts(remoteProducts.products.map { it.toEntity() })
        emit(remoteProducts.products.map { it.toDomain() })
    }

    override fun getLocalProducts(query: String): Flow<List<Product>> {
        val localProduct = productDao.searchProducts(query)
        return localProduct.map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun fetchInitialProducts() {
        val response = apiService.getAllProducts()
        productDao.insertProducts(response.products.map { it.toEntity() })
    }
}