package com.example.initial_quest.data.repository

import com.example.initial_quest.data.local.dao.EventDao
import com.example.initial_quest.data.local.entity.Event
import com.example.initial_quest.data.api.EventApiService
import com.example.initial_quest.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventApiService: EventApiService, private val eventDao: EventDao
) : EventRepository {

    override fun getEvents(): Flow<List<Event>> = eventDao.getEvents()

    override fun getEvent(id: Int): Flow<Event> = eventDao.getEvent(id)

    override suspend fun fetchAndStoreEvents() {
        val events = eventApiService.getEvents()
        eventDao.insertEvents(events)
    }
}