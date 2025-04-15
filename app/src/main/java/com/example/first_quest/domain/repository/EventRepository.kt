package com.example.first_quest.domain.repository

import com.example.first_quest.data.local.entity.Event
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun getEvents(): Flow<List<Event>>
    fun getEvent(id: Int): Flow<Event>
    suspend fun fetchAndStoreEvents()
}