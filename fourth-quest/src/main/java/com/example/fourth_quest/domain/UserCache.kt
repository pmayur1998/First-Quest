package com.example.fourth_quest.domain

import kotlinx.coroutines.flow.SharedFlow

interface UserCache {
    suspend fun getUser(userId: String): User?
    suspend fun putUser(user: User)
    suspend fun removeUser(userId: String)
    fun observeUsers(): SharedFlow<List<User>>
    suspend fun initializeCache(users: List<User>)
}