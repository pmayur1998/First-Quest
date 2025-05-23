package com.example.third_quest.domain.usecase

import com.example.third_quest.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import javax.inject.Inject

class TrackUserLoyaltyPointsUseCase @Inject constructor(
    private val repository: StatsRepository
) {
    operator fun invoke(userId: String): Flow<List<Int>> {
        return repository.getUserOrders(userId).map { userOrders ->
            val sortedOrders = userOrders.sortedBy { it.timestamp }

            sortedOrders.scan(10) { pointsBalance, order ->
                val orderPoints = order.items.fold(BigDecimal.ZERO) { total, item ->
                    total.add(item.getTotalCost())
                }.toInt()

                pointsBalance + orderPoints
            }
        }
    }
}