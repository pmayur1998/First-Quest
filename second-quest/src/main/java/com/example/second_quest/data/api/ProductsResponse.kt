package com.example.second_quest.data.api

data class ProductsResponse(
    val products: List<ProductDto>, val total: Int, val skip: Int, val limit: Int
)

data class ProductDto(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Int,
    val brand: String?,
    val category: String,
    val thumbnail: String
)