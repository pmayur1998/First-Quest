package com.example.fourth_quest.data.repository

import app.cash.turbine.test
import com.example.fourth_quest.domain.User
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class InMemoryUserRepositoryTest {

    private lateinit var repository: InMemoryUserRepository
    private val testUser = User("1", "abc", "abc@google.com")
    private val testUser2 = User("2", "xyz", "xyz@egoogle.com")

    @Before
    fun setup() {
        repository = InMemoryUserRepository()
    }

    @Test
    fun `fetchUserById should return user if exists`() = runTest {
        repository.saveUser(testUser)

        val result = repository.fetchUserById("1")
        assertThat(result).isEqualTo(testUser)
    }

    @Test
    fun `fetchUserById should return null if user does not exist`() = runTest {
        val result = repository.fetchUserById("3")
        assertThat(result).isNull()
    }

    @Test
    fun `fetchAllUsers should return all saved users`() = runTest {
        repository.saveUser(testUser)
        repository.saveUser(testUser2)

        repository.fetchAllUsers().test {
            val items = mutableListOf<User>()
            items.add(awaitItem())
            items.add(awaitItem())
            awaitComplete()
            assertThat(items).containsExactlyElementsIn(listOf(testUser, testUser2))
        }
    }

    @Test
    fun `saveUser should add or update user`() = runTest {
        val result = repository.saveUser(testUser)
        assertThat(result).isEqualTo(testUser)
        assertThat(repository.fetchUserById("1")).isEqualTo(testUser)
    }

    @Test
    fun `deleteUser should remove user`() = runTest {
        repository.saveUser(testUser)

        val result = repository.deleteUser("1")
        assertThat(result).isTrue()
        assertThat(repository.fetchUserById("1")).isNull()
    }

    @Test
    fun `deleteUser should return false if user not found`() = runTest {
        val result = repository.deleteUser("3")
        assertThat(result).isFalse()
    }
}