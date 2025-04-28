package com.example.second_quest.domain.model

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Int,
    val brand: String,
    val category: String,
    val thumbnail: String
)

data class FilterParams(
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val sortOrder: SortOrder = SortOrder.DEFAULT
)

enum class SortOrder {
    DEFAULT, PRICE_ASC, PRICE_DESC, NAME_ASC, NAME_DESC
}