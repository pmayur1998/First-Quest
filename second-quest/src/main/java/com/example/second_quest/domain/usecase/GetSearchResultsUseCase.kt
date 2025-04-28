package com.example.second_quest.domain.usecase

import com.example.common.domain.model.Result
import com.example.common.utils.ConnectivityChecker
import com.example.second_quest.domain.model.FilterParams
import com.example.second_quest.domain.model.Product
import com.example.second_quest.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSearchResultsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val connectivityChecker: ConnectivityChecker,
    private val filterProductUseCase: FilterProductUseCase
) {
    operator fun invoke(
        searchQuery: String, filterParams: FilterParams
    ): Flow<Result<List<Product>>> {
        return if (connectivityChecker.isNetworkAvailable() && searchQuery.isNotEmpty()) {
            getRemoteAndLocalProductsFlow(searchQuery, filterParams)
        } else {
            getLocalProductsFlow(searchQuery, filterParams)
        }
    }

    private fun getRemoteAndLocalProductsFlow(
        searchQuery: String, filterParams: FilterParams
    ): Flow<Result<List<Product>>> {
        return combine(
            productRepository.getRemoteProducts(searchQuery),
            productRepository.getLocalProducts(searchQuery)
        ) { apiProducts, dbProducts ->
            val products = (apiProducts + dbProducts).distinctBy { it.id }
            Result.Success(filterProductUseCase(products, filterParams))
        }.catch { e ->
            Result.Error(e)
        }
    }

    private fun getLocalProductsFlow(
        searchQuery: String, filterParams: FilterParams
    ): Flow<Result<List<Product>>> {
        return productRepository.getLocalProducts(searchQuery)
            .map { Result.Success(filterProductUseCase(it, filterParams)) }
            .catch { e -> Result.Error(e) }
    }
}