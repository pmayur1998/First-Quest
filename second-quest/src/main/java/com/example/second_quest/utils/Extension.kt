package com.example.second_quest.utils

fun String.convertToPriceDouble(): Double? =
    takeIf { it.isNotEmpty() && !startsWith(".0") }?.toDoubleOrNull()
