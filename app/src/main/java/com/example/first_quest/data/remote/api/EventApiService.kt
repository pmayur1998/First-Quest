package com.example.first_quest.data.remote.api

import com.example.first_quest.data.local.entity.Event
import retrofit2.http.GET

interface EventApiService {
    @GET("posts")
    suspend fun getEvents(): List<Event>
}