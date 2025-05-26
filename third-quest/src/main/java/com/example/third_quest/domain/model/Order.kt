package com.example.third_quest.domain.model

import org.threeten.bp.LocalDateTime

data class Order(
    val orderId: String,
    val userId: String,
    val items: List<OrderItem>,
    val timestamp: LocalDateTime
)
