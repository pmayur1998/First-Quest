package com.example.third_quest.ui.loyalty_points

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.third_quest.domain.usecase.TrackUserLoyaltyPointsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoyaltyPointsViewModel @Inject constructor(
    private val trackUserLoyaltyPointsUseCase: TrackUserLoyaltyPointsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<List<Int>>(emptyList())
    val uiState: StateFlow<List<Int>> = _uiState.asStateFlow()

    init {
        savedStateHandle.get<String>("userId")?.let { userId ->
            getUserLoyaltyPoints(userId)
        }
    }

    private fun getUserLoyaltyPoints(userId: String) {
        viewModelScope.launch {
            trackUserLoyaltyPointsUseCase(userId).collect { pointsHistory ->
                _uiState.update { pointsHistory }
            }
        }
    }
}