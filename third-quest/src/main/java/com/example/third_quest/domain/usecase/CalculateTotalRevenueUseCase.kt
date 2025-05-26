package com.example.third_quest.domain.usecase

import com.example.third_quest.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import javax.inject.Inject

class CalculateTotalRevenueUseCase @Inject constructor(
    private val repository: StatsRepository
) {
    operator fun invoke(): Flow<BigDecimal> {
        return repository.getOrders().map { orders ->
            orders.fold(BigDecimal.ZERO) { totalRevenue, order ->
                val orderTotal = order.items.fold(BigDecimal.ZERO) { itemTotal, item ->
                    itemTotal.add(item.pricePerUnit.multiply(BigDecimal(item.quantity)))
                }
                totalRevenue.add(orderTotal)
            }
        }
    }
}