package com.example.third_quest.domain.usecase

import com.example.third_quest.domain.model.OrderItem
import com.example.third_quest.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FindMostExpensiveOrderItemUseCase @Inject constructor(
    private val repository: StatsRepository
) {
    operator fun invoke(): Flow<OrderItem?> {
        return repository.getOrders().map { orders ->
            orders.flatMap { it.items }.reduce { mostExpensive, current ->
                if (current.getTotalCost() > mostExpensive.getTotalCost()) current
                else mostExpensive
            }
        }
    }
}
