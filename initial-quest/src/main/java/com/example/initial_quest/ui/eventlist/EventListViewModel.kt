package com.example.initial_quest.ui.eventlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.initial_quest.data.local.entity.Event
import com.example.common.domain.model.Result
import com.example.initial_quest.domain.usecase.FetchAndStoreEvents
import com.example.initial_quest.domain.usecase.GetEventsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventListViewModel @Inject constructor(
    private val getEventsUseCase: GetEventsUseCase,
    private val fetchEventsUseCase: FetchAndStoreEvents
) : ViewModel() {

    private val _uiState = MutableStateFlow<EventListUiState>(EventListUiState.Loading)
    val uiState: StateFlow<EventListUiState> = _uiState.asStateFlow()

    init {
        fetchEvents()
    }

    private fun fetchEvents() {
        viewModelScope.launch {
            when (val result = fetchEventsUseCase()) {
                is Result.Success -> getEvents()
                is Result.Error -> _uiState.value = EventListUiState.Error(result.exception.message)
                is Result.Loading -> _uiState.value = EventListUiState.Loading
            }
        }
    }

    private fun getEvents() {
        getEventsUseCase().onEach { result ->
            _uiState.value = when (result) {
                is Result.Success -> EventListUiState.Success(result.data)
                is Result.Error -> EventListUiState.Error(result.exception.message)
                is Result.Loading -> EventListUiState.Loading
            }
        }.launchIn(viewModelScope)
    }
}

sealed class EventListUiState {
    data object Loading : EventListUiState()
    data class Success(val events: List<Event>) : EventListUiState()
    data class Error(val message: String?) : EventListUiState()
}