package com.example.third_quest.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.third_quest.domain.model.OrderItem
import com.example.third_quest.domain.model.UserSpending
import com.example.third_quest.domain.usecase.CalculateTotalRevenueUseCase
import com.example.third_quest.domain.usecase.FindMostExpensiveOrderItemUseCase
import com.example.third_quest.domain.usecase.GetProductSalesCountUseCase
import com.example.third_quest.domain.usecase.GetUniqueProductIdsUseCase
import com.example.third_quest.domain.usecase.SummarizeUserSpendingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val calculateTotalRevenueUseCase: CalculateTotalRevenueUseCase,
    private val findMostExpensiveOrderItemUseCase: FindMostExpensiveOrderItemUseCase,
    private val getUniqueProductIdsUseCase: GetUniqueProductIdsUseCase,
    private val getProductSalesCountUseCase: GetProductSalesCountUseCase,
    private val summarizeUserSpendingUseCase: SummarizeUserSpendingUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            combine(
                calculateTotalRevenueUseCase(),
                findMostExpensiveOrderItemUseCase(),
                getUniqueProductIdsUseCase(),
                getProductSalesCountUseCase(),
                summarizeUserSpendingUseCase()
            ) { totalRevenue, mostExpensiveItem, uniqueProductIds, productSalesCount, userSpending ->
                StatsUiState(
                    isLoading = false,
                    totalRevenue = totalRevenue,
                    mostExpensiveItem = mostExpensiveItem,
                    uniqueProductIds = uniqueProductIds,
                    productSalesCount = productSalesCount,
                    userSpending = userSpending
                )
            }.catch { error ->
                _uiState.update {
                    it.copy(isLoading = false, error = error.message)
                }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}

data class StatsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val totalRevenue: BigDecimal = BigDecimal.ZERO,
    val mostExpensiveItem: OrderItem? = null,
    val uniqueProductIds: Set<String> = emptySet(),
    val productSalesCount: Map<String, Int> = emptyMap(),
    val userSpending: List<UserSpending> = emptyList()
)