package com.example.fourth_quest.data.repository

import com.example.fourth_quest.domain.User
import com.example.fourth_quest.domain.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryUserRepository : UserRepository {
    private val users = mutableMapOf<String, User>()
    private val mutex = Mutex()

    override suspend fun fetchUserById(userId: String): User? = mutex.withLock {
        delay(100)
        users[userId]
    }

    override suspend fun fetchAllUsers(): Flow<User> = mutex.withLock {
        delay(200)
        users.values.asFlow()
    }

    override suspend fun saveUser(user: User): User = mutex.withLock {
        delay(150)
        users[user.id] = user
        user
    }

    override suspend fun deleteUser(userId: String): Boolean = mutex.withLock {
        delay(100)
        users.remove(userId) != null
    }

    suspend fun clear() = mutex.withLock {
        users.clear()
    }
}