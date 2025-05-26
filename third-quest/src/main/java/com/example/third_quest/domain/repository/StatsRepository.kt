package com.example.third_quest.domain.repository

import com.example.third_quest.domain.model.Order
import com.example.third_quest.domain.model.User
import kotlinx.coroutines.flow.Flow

interface StatsRepository {
    fun getOrders(): Flow<List<Order>>
    fun getUsers(): Flow<List<User>>
    fun getUserOrders(userId: String): Flow<List<Order>>
}