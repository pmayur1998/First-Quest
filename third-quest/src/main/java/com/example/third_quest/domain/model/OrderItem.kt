package com.example.third_quest.domain.model

import java.math.BigDecimal

data class OrderItem(
    val productId: String, val quantity: Int, val pricePerUnit: BigDecimal
) {
    fun getTotalCost(): BigDecimal = pricePerUnit.multiply(BigDecimal(quantity))
}
