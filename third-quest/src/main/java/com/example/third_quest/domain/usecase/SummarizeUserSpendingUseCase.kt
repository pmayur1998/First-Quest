package com.example.third_quest.domain.usecase

import com.example.third_quest.domain.model.UserSpending
import com.example.third_quest.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.math.BigDecimal
import javax.inject.Inject

class SummarizeUserSpendingUseCase @Inject constructor(
    private val repository: StatsRepository
) {
    operator fun invoke(): Flow<List<UserSpending>> = combine(
        repository.getUsers(), repository.getOrders()
    ) { users, orders ->
        val userIdToUsername = users.associateBy({ it.userId }, { it.username })
        val usersSpending = mutableMapOf<String, BigDecimal>()

        orders.forEach { order ->
            val orderTotal = order.items.fold(BigDecimal.ZERO) { total, item ->
                total.add(item.getTotalCost())
            }

            usersSpending[order.userId] =
                (usersSpending[order.userId] ?: BigDecimal.ZERO).add(orderTotal)
        }

        usersSpending.mapNotNull { (userId, totalSpent) ->
            userIdToUsername[userId]?.let { username ->
                UserSpending(userId, username, totalSpent)
            }
        }
    }
}
