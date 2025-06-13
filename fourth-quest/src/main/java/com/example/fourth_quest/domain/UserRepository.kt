package com.example.fourth_quest.domain

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun fetchUserById(userId: String): User?
    suspend fun fetchAllUsers(): Flow<User>
    suspend fun saveUser(user: User): User
    suspend fun deleteUser(userId: String): Boolean
}