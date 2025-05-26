package com.example.third_quest.data.repository

import com.example.third_quest.domain.model.Order
import com.example.third_quest.domain.model.OrderItem
import com.example.third_quest.domain.model.User
import com.example.third_quest.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.threeten.bp.LocalDateTime
import org.threeten.bp.temporal.ChronoUnit
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class StatsRepositoryImpl @Inject constructor() : StatsRepository {

    //Mocked Data
    private val productIds = List(10) { "${it + 1001}" }
    private val users = List(15) { index ->
        User(
            userId = "${index + 1}", username = "User ${index + 1}"
        )
    }
    private val orders = generateMockOrders()

    private fun generateMockOrders(): List<Order> {
        val now = LocalDateTime.now()
        return List(50) { index ->
            val userId = users[Random.nextInt(users.size)].userId
            val itemCount = Random.nextInt(1, 10)
            val orderItems = List(itemCount) {
                OrderItem(
                    productId = productIds[Random.nextInt(productIds.size)],
                    quantity = Random.nextInt(1, 10),
                    pricePerUnit = BigDecimal(Random.nextDouble(5.0, 100.0)).setScale(
                        2, RoundingMode.HALF_UP
                    )
                )
            }

            Order(
                orderId = "${index + 1}",
                userId = userId,
                items = orderItems,
                timestamp = now.minus(Random.nextLong(1, 90), ChronoUnit.DAYS)
            )
        }
    }

    override fun getOrders(): Flow<List<Order>> = flowOf(orders)

    override fun getUsers(): Flow<List<User>> = flowOf(users)

    override fun getUserOrders(userId: String): Flow<List<Order>> {
        return flowOf(orders.filter { it.userId == userId })
    }
}