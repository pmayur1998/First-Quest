package com.example.first_quest.domain.usecase

import com.example.first_quest.data.local.entity.Event
import com.example.first_quest.domain.model.Result
import com.example.first_quest.domain.repository.EventRepository
import com.example.first_quest.utils.runSuspendCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetEventsUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    operator fun invoke(): Flow<Result<List<Event>>> = flow {
        runSuspendCatching {
            eventRepository.getEvents().collect {
                emit(Result.Success(it))
            }
        }.onFailure {
            emit(Result.Error(it))
        }
    }
}