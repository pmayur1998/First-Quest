package com.example.third_quest.domain.model

import java.math.BigDecimal

data class UserSpending(
    val userId: String,
    val userName: String,
    val totalSpent: BigDecimal
)