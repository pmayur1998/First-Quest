package com.example.first_quest.data.repository

import com.example.first_quest.data.local.dao.EventDao
import com.example.first_quest.data.local.entity.Event
import com.example.first_quest.data.remote.api.EventApiService
import com.example.first_quest.domain.repository.EventRepository
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