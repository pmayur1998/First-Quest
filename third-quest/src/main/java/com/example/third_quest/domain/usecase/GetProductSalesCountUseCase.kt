package com.example.third_quest.domain.usecase

import com.example.third_quest.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetProductSalesCountUseCase @Inject constructor(
    private val repository: StatsRepository
) {
    operator fun invoke(): Flow<Map<String, Int>> {
        return repository.getOrders().map { orders ->
            orders.flatMap { it.items }.fold(mutableMapOf()) { acc, item ->
                acc[item.productId] = (acc[item.productId] ?: 0) + item.quantity
                acc
            }
        }
    }
}