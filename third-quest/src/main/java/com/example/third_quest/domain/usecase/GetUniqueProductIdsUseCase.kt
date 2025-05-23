package com.example.third_quest.domain.usecase

import com.example.third_quest.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetUniqueProductIdsUseCase @Inject constructor(
    private val repository: StatsRepository
) {
    operator fun invoke(): Flow<Set<String>> {
        return repository.getOrders().map { orders ->
            orders.flatMap { it.items }.map { it.productId }.toSet()
        }
    }
}