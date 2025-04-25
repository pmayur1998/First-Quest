package com.example.initial_quest.data.api

import com.example.initial_quest.data.local.entity.Event
import retrofit2.http.GET

interface EventApiService {
    @GET("posts")
    suspend fun getEvents(): List<Event>
}