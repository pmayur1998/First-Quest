package com.example.initial_quest.ui.eventdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.initial_quest.data.local.entity.Event
import com.example.common.domain.model.Result
import com.example.initial_quest.domain.usecase.GetEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val getEventUseCase: GetEventUseCase, savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<EventDetailUiState>(EventDetailUiState.Loading)
    val uiState: StateFlow<EventDetailUiState> = _uiState.asStateFlow()

    init {
        savedStateHandle.get<Int>("eventId")?.let { eventId ->
            getEvent(eventId)
        }
    }

    private fun getEvent(id: Int) {
        getEventUseCase(id).onEach { result ->
            _uiState.value = when (result) {
                is Result.Success -> EventDetailUiState.Success(result.data)
                is Result.Error -> EventDetailUiState.Error(
                    result.exception.message
                )

                is Result.Loading -> EventDetailUiState.Loading
            }
        }.launchIn(viewModelScope)
    }
}

sealed class EventDetailUiState {
    data object Loading : EventDetailUiState()
    data class Success(val event: Event) : EventDetailUiState()
    data class Error(val message: String?) : EventDetailUiState()
}