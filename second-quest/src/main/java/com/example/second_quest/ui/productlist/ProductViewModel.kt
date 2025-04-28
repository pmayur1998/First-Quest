package com.example.second_quest.ui.productlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.domain.model.Result
import com.example.second_quest.domain.model.FilterParams
import com.example.second_quest.domain.model.Product
import com.example.second_quest.domain.model.SortOrder
import com.example.second_quest.domain.usecase.FilterProductUseCase
import com.example.second_quest.domain.usecase.GetSearchResultsUseCase
import com.example.second_quest.domain.usecase.RefreshProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getProductsUseCase: GetSearchResultsUseCase,
    private val refreshProductsUseCase: RefreshProductsUseCase,
    private val filterProductUseCase: FilterProductUseCase
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    private var fetchedProducts: List<Product> = emptyList()
    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchInitialProducts()
    }

    private fun fetchInitialProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            handleUIAsPerResult(refreshProductsUseCase())
            setupSearchObserver()
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun setupSearchObserver() {
        _searchQuery.debounce(300).filter { it.length > 2 || it.isEmpty() }.distinctUntilChanged()
            .flatMapLatest { query ->
                getProductsUseCase(query, getFilterParams())
            }.onEach { result ->
                handleUIAsPerResult(result)
            }.launchIn(viewModelScope)
    }

    fun handleEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> {
                _searchQuery.update { event.query }
                _uiState.update { it.copy(searchQuery = event.query) }
            }

            is SearchEvent.PriceRangeChanged -> {
                _uiState.update { it.copy(minPrice = event.min, maxPrice = event.max) }
                filterProducts()
            }

            is SearchEvent.SortOrderChanged -> {
                _uiState.update { it.copy(sortOrder = event.sortOrder) }
                filterProducts()
            }

            is SearchEvent.RefreshRequested -> refreshProducts()
            is SearchEvent.ErrorDismissed -> _uiState.update { it.copy(error = null) }

        }
    }

    private fun refreshProducts() {
        viewModelScope.launch {
            _uiState.update { ProductsUiState(isLoading = true) }
            handleUIAsPerResult(refreshProductsUseCase(true))
        }
    }

    private fun filterProducts() {
        val result = filterProductUseCase(fetchedProducts, getFilterParams())
        _uiState.update { it.copy(products = result, needToScrollTop = true) }
    }

    private fun getFilterParams(): FilterParams {
        val state = _uiState.value
        return FilterParams(
            minPrice = state.minPrice, maxPrice = state.maxPrice, sortOrder = state.sortOrder
        )
    }

    private fun handleUIAsPerResult(result: Result<List<Product>>) {
        when (result) {
            is Result.Success -> {
                _uiState.update { state ->
                    fetchedProducts = result.data
                    state.copy(products = result.data, isLoading = false, error = null)
                }
            }

            is Result.Error -> {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false, error = result.exception.message ?: "Unknown error"
                    )
                }
            }

            Result.Loading -> {
                _uiState.update { it.copy(isLoading = true) }
            }
        }
    }
}


data class ProductsUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val sortOrder: SortOrder = SortOrder.DEFAULT,
    val needToScrollTop: Boolean = false
)

sealed class SearchEvent {
    data class QueryChanged(val query: String) : SearchEvent()
    data class PriceRangeChanged(val min: Double?, val max: Double?) : SearchEvent()
    data class SortOrderChanged(val sortOrder: SortOrder) : SearchEvent()
    data object RefreshRequested : SearchEvent()
    data object ErrorDismissed : SearchEvent()
}