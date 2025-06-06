package com.example.initial_quest.domain.usecase

import com.example.initial_quest.data.local.entity.Event
import com.example.common.domain.model.Result
import com.example.initial_quest.domain.repository.EventRepository
import com.example.common.utils.runSuspendCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetEventUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    operator fun invoke(id: Int): Flow<Result<Event>> = flow {
        runSuspendCatching {
            eventRepository.getEvent(id).collect {
                emit(Result.Success(it))
            }
        }.onFailure {
            emit(Result.Error(it))
        }
    }
}