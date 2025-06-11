package com.example.fourth_quest.manager

import com.example.common.utils.runSuspendCatching
import com.example.fourth_quest.domain.User
import com.example.fourth_quest.domain.UserCache
import com.example.fourth_quest.domain.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.onEach

class UserManager(
    private val userRepository: UserRepository, private val userCache: UserCache
) {

    suspend fun getUser(userId: String): User? {
        return runSuspendCatching {
            userCache.getUser(userId) ?: userRepository.fetchUserById(userId)?.let { fetchedUser ->
                runCatching {
                    userCache.putUser(fetchedUser)
                    fetchedUser
                }.getOrNull()
            }
        }.getOrNull()
    }

    suspend fun refreshAllUsers(): Flow<User> {
        return runSuspendCatching {
            userRepository.fetchAllUsers()
                .onEach { user -> runCatching { userCache.putUser(user) } }
        }.getOrElse { emptyFlow() }
    }

    fun observeAllUsers(): SharedFlow<List<User>> {
        return runCatching { userCache.observeUsers() }.getOrElse { MutableSharedFlow<List<User>>().asSharedFlow() }
    }

    suspend fun saveUser(user: User): User {
        return runSuspendCatching {
            val savedUser = userRepository.saveUser(user)
            userCache.putUser(savedUser)
            savedUser
        }.getOrThrow()
    }

    suspend fun deleteUser(userId: String): Boolean {
        return runSuspendCatching {
            val isDeleted = userRepository.deleteUser(userId)
            if (isDeleted) {
                userCache.removeUser(userId)
            }
            isDeleted
        }.getOrElse { false }
    }
}