package com.example.fourth_quest.data.cache

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
class InMemoryUserCacheTest {

    private lateinit var cache: InMemoryUserCache
    private val testUser = User("1", "abc", "abc@google.com")
    private val testUser2 = User("2", "xyz", "xyz@egoogle.com")

    @Before
    fun setup() {
        cache = InMemoryUserCache()
    }

    @Test
    fun `getUser should return user if exists`() = runTest {
        cache.putUser(testUser)

        val result = cache.getUser("1")
        assertThat(result).isEqualTo(testUser)
    }

    @Test
    fun `getUser should return null if user does not exist`() = runTest {
        val result = cache.getUser("3")
        assertThat(result).isNull()
    }

    @Test
    fun `putUser should add user and emit updated list`() = runTest {
        cache.observeUsers().test {
            assertThat(awaitItem()).isEmpty()
            cache.putUser(testUser)
            assertThat(awaitItem()).containsExactly(testUser)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `removeUser should remove user and emit updated list`() = runTest {
        cache.putUser(testUser)
        cache.putUser(testUser2)

        cache.observeUsers().test {
            assertThat(awaitItem()).containsExactlyElementsIn(listOf(testUser, testUser2))
            cache.removeUser("1")
            assertThat(awaitItem()).containsExactly(testUser2)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observeUsers should emit current and subsequent lists`() = runTest {
        cache.observeUsers().test {
            assertThat(awaitItem()).isEmpty()
            cache.putUser(testUser)
            assertThat(awaitItem()).containsExactly(testUser)
            cache.putUser(testUser2)
            assertThat(awaitItem()).containsExactly(testUser, testUser2)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initializeCache should clear existing and populate with new users`() = runTest {
        cache.putUser(testUser)

        cache.observeUsers().test {
            assertThat(awaitItem()).containsExactly(testUser)
            cache.initializeCache(listOf(testUser2))
            assertThat(awaitItem()).containsExactly(testUser2)
            cancelAndIgnoreRemainingEvents()
        }
    }
}