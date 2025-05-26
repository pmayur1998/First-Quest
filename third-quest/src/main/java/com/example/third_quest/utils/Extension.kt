package com.example.third_quest.utils

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

fun BigDecimal.formatCurrency(): String {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    return currencyFormat.format(this)
}