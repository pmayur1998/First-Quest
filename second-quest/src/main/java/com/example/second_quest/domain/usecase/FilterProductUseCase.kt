package com.example.second_quest.domain.usecase

import com.example.second_quest.domain.model.FilterParams
import com.example.second_quest.domain.model.Product
import com.example.second_quest.domain.model.SortOrder
import javax.inject.Inject

class FilterProductUseCase @Inject constructor() {
    operator fun invoke(products: List<Product>, params: FilterParams): List<Product> {
        return products.filter { product ->
            (params.minPrice == null || product.price >= params.minPrice) && (params.maxPrice == null || product.price <= params.maxPrice)
        }.let { filtered ->
            when (params.sortOrder) {
                SortOrder.PRICE_ASC -> filtered.sortedBy { it.price }
                SortOrder.PRICE_DESC -> filtered.sortedByDescending { it.price }
                SortOrder.NAME_ASC -> filtered.sortedBy { it.title }
                SortOrder.NAME_DESC -> filtered.sortedByDescending { it.title }
                else -> filtered
            }
        }
    }
}