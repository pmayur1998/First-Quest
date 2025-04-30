package com.example.second_quest.domain.usecase

import com.example.common.domain.model.Result
import com.example.common.utils.ConnectivityChecker
import com.example.common.utils.runSuspendCatching
import com.example.second_quest.domain.repository.ProductRepository
import javax.inject.Inject

class FetchInitialProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val connectivityChecker: ConnectivityChecker
) {
    suspend operator fun invoke(): Result<Unit> {
        if (!connectivityChecker.isNetworkAvailable()) {
            return Result.Error(Throwable("No internet connection"))
        }
        return runSuspendCatching { productRepository.fetchInitialProducts() }.fold(onSuccess = {
            Result.Success(it)
        }, onFailure = {
            Result.Error(it)
        })
    }
}