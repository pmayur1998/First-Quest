package com.example.fourth_quest.manager

import com.example.fourth_quest.domain.User
import com.example.fourth_quest.domain.UserCache
import com.example.fourth_quest.domain.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.onEach

class UserManager(
    private val userRepository: UserRepository, private val userCache: UserCache
) {

    suspend fun getUser(userId: String): User? {
        return userCache.getUser(userId) ?: userRepository.fetchUserById(userId)
            ?.also { fetchedUser ->
                userCache.putUser(fetchedUser)
            }
    }

    suspend fun refreshAllUsers(): Flow<User> {
        return userRepository.fetchAllUsers().onEach { user -> userCache.putUser(user) }
    }

    fun observeAllUsers(): SharedFlow<List<User>> = userCache.observeUsers()

    suspend fun saveUser(user: User): User {
        return userRepository.saveUser(user).also { savedUser ->
            userCache.putUser(savedUser)
        }
    }

    suspend fun deleteUser(userId: String): Boolean {
        return userRepository.deleteUser(userId).also { isDeleted ->
            if (isDeleted) userCache.removeUser(userId)
        }
    }
}