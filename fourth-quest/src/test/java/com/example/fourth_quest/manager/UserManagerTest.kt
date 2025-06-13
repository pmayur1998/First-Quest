package com.example.fourth_quest.manager

import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import com.google.common.truth.Truth.assertThat
import app.cash.turbine.test
import com.example.fourth_quest.domain.User
import com.example.fourth_quest.domain.UserCache
import com.example.fourth_quest.domain.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.toList
import org.junit.Assert.assertThrows
import java.io.IOException
import java.net.UnknownHostException

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class UserManagerTest {

    private lateinit var userManager: UserManager
    private val userRepository: UserRepository = mockk()
    private val userCache: UserCache = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private val testUser = User("1", "abc", "abc@google.com")
    private val testUser2 = User("2", "xyz", "xyz@egoogle.com")

    @Before
    fun setup() {
        userManager = UserManager(userRepository, userCache)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `getUser should return user from cache if available`() = runTest(testDispatcher) {
        coEvery { userCache.getUser("1") } returns testUser

        val result = userManager.getUser("1")
        assertThat(result).isEqualTo(testUser)
        coVerify(exactly = 1) { userCache.getUser("1") }
        coVerify(exactly = 0) { userRepository.fetchUserById(any()) }
    }

    @Test
    fun `getUser should fetch from repository and cache it if not in cache`() = runTest(testDispatcher) {
        coEvery { userCache.getUser("1") } returns null
        coEvery { userRepository.fetchUserById("1") } returns testUser
        coJustRun { userCache.putUser(testUser) }

        val result = userManager.getUser("1")
        assertThat(result).isEqualTo(testUser)
        coVerifyOrder {
            userCache.getUser("1")
            userRepository.fetchUserById("1")
            userCache.putUser(testUser)
        }
    }

    @Test
    fun `getUser should return null if user not found in cache or repository`() = runTest(testDispatcher) {
        coEvery { userCache.getUser("1") } returns null
        coEvery { userRepository.fetchUserById("1") } returns null

        val result = userManager.getUser("1")
        assertThat(result).isNull()
        coVerify { userCache.getUser("1") }
        coVerify { userRepository.fetchUserById("1") }
        coVerify(exactly = 0) { userCache.putUser(any()) }
    }

    @Test
    fun `getUser should throw exception when userCache getUser throws exception`() = runTest(testDispatcher) {
        coEvery { userCache.getUser("1") } throws RuntimeException("Cache corruption")

        assertThrows(RuntimeException::class.java) {
            runTest(testDispatcher) {
                userManager.getUser("1")
            }
        }

        coVerify { userCache.getUser("1") }
        coVerify(exactly = 0) { userRepository.fetchUserById(any()) }
    }

    @Test
    fun `getUser should throw exception when userRepository fetchUserById throws exception`() = runTest(testDispatcher) {
        coEvery { userCache.getUser("1") } returns null
        coEvery { userRepository.fetchUserById("1") } throws UnknownHostException("Network error")

        assertThrows(UnknownHostException::class.java) {
            runTest(testDispatcher) {
                userManager.getUser("1")
            }
        }
        coVerifyOrder {
            userCache.getUser("1")
            userRepository.fetchUserById("1")
        }
        coVerify(exactly = 0) { userCache.putUser(any()) }
    }

    @Test
    fun `getUser should throw exception when userCache putUser throws exception`() = runTest(testDispatcher) {
        coEvery { userCache.getUser("1") } returns null
        coEvery { userRepository.fetchUserById("1") } returns testUser
        coEvery { userCache.putUser(testUser) } throws RuntimeException("Cache full")

        assertThrows(RuntimeException::class.java) {
            runTest(testDispatcher) {
                userManager.getUser("1")
            }
        }
        coVerifyOrder {
            userCache.getUser("1")
            userRepository.fetchUserById("1")
            userCache.putUser(testUser)
        }
    }

    @Test
    fun `refreshAllUsers should fetch from repository and update cache`() = runTest(testDispatcher) {
        val userFlow = flowOf(testUser, testUser2)
        coEvery { userRepository.fetchAllUsers() } returns userFlow
        coJustRun { userCache.initializeCache(userFlow.toList()) }

        userManager.refreshAllUsers().test {
            assertThat(awaitItem()).isEqualTo(testUser)
            assertThat(awaitItem()).isEqualTo(testUser2)
            awaitComplete()
        }

        coVerify { userCache.initializeCache(userFlow.toList()) }
    }

    @Test
    fun `refreshAllUsers should throw exception when userRepository fetchAllUsers throws exception`() = runTest(testDispatcher) {
        coEvery { userRepository.fetchAllUsers() } throws IOException("Network error")

        assertThrows(IOException::class.java) {
            runTest(testDispatcher) {
                userManager.refreshAllUsers()
            }
        }
        coVerify { userRepository.fetchAllUsers() }
        coVerify(exactly = 0) { userCache.putUser(any()) }
    }

    @Test
    fun `refreshAllUsers should handle empty repository response`() = runTest(testDispatcher) {
        val emptyFlow = emptyFlow<User>()
        coEvery { userRepository.fetchAllUsers() } returns emptyFlow
        coJustRun { userCache.initializeCache(emptyList()) }

        val result = userManager.refreshAllUsers().toList()
        assertThat(result).isEmpty()
    }

    @Test
    fun `observeAllUsers should emit current cached users`() = runTest(testDispatcher) {
        val usersFlow = MutableSharedFlow<List<User>>()
        every { userCache.observeUsers() } returns usersFlow

        val result = userManager.observeAllUsers()

        assertThat(result).isEqualTo(usersFlow)
        verify { userCache.observeUsers() }
    }

    @Test
    fun `observeAllUsers should throw exception when userCache observeUsers throws exception`() = runTest(testDispatcher) {
        every { userCache.observeUsers() } throws RuntimeException("Cache observation failed")

        assertThrows(RuntimeException::class.java) {
            runTest(testDispatcher) {
                userManager.observeAllUsers()
            }
        }
        verify { userCache.observeUsers() }
    }

    @Test
    fun `saveUser should save to repository and update cache`() = runTest(testDispatcher) {
        coEvery { userRepository.saveUser(testUser) } returns testUser
        coJustRun { userCache.putUser(testUser) }

        val result = userManager.saveUser(testUser)

        assertThat(result).isEqualTo(testUser)
        coVerifyOrder {
            userRepository.saveUser(testUser)
            userCache.putUser(testUser)
        }
    }

    @Test
    fun `saveUser should throw exception when userRepository saveUser throws exception`() = runTest(testDispatcher) {
        coEvery { userRepository.saveUser(testUser) } throws IOException("Network error")

        assertThrows(IOException::class.java) {
            runTest(testDispatcher) {
                userManager.saveUser(testUser)
            }
        }

        coVerify { userRepository.saveUser(testUser) }
        coVerify(exactly = 0) { userCache.putUser(any()) }
    }

    @Test
    fun `saveUser should throw exception when userCache putUser throws exception`() = runTest(testDispatcher) {
        coEvery { userRepository.saveUser(testUser) } returns testUser
        coEvery { userCache.putUser(testUser) } throws RuntimeException("Cache error")

        assertThrows(RuntimeException::class.java) {
            runTest(testDispatcher) {
                userManager.saveUser(testUser)
            }
        }

        coVerifyOrder {
            userRepository.saveUser(testUser)
            userCache.putUser(testUser)
        }
    }

    @Test
    fun `deleteUser should delete from repository and remove from cache`() = runTest(testDispatcher) {
        coEvery { userRepository.deleteUser("1") } returns true
        coJustRun { userCache.removeUser("1") }

        val result = userManager.deleteUser("1")

        assertThat(result).isTrue()
        coVerifyOrder {
            userRepository.deleteUser("1")
            userCache.removeUser("1")
        }
    }

    @Test
    fun `deleteUser should return false when repository deletion fails`() = runTest(testDispatcher) {
        coEvery { userRepository.deleteUser("1") } returns false

        val result = userManager.deleteUser("1")

        assertThat(result).isFalse()
        coVerify { userRepository.deleteUser("1") }
        coVerify(exactly = 0) { userCache.removeUser(any()) }
    }

    @Test
    fun `deleteUser should throw exception when userRepository deleteUser throws exception`() = runTest(testDispatcher) {
        coEvery { userRepository.deleteUser("1") } throws IOException("Network error")

        assertThrows(IOException::class.java) {
            runTest(testDispatcher) {
                userManager.deleteUser("1")
            }
        }

        coVerify { userRepository.deleteUser("1") }
        coVerify(exactly = 0) { userCache.removeUser(any()) }
    }

    @Test
    fun `deleteUser should throw exception when userCache removeUser throws exception`() = runTest(testDispatcher) {
        coEvery { userRepository.deleteUser("1") } returns true
        coEvery { userCache.removeUser("1") } throws RuntimeException("Cache remove error")

        assertThrows(RuntimeException::class.java) {
            runTest(testDispatcher) {
                userManager.deleteUser("1")
            }
        }

        coVerifyOrder {
            userRepository.deleteUser("1")
            userCache.removeUser("1")
        }
    }

    @Test
    fun `saveUser followed by getUser should work correctly`() = runTest(testDispatcher) {
        coEvery { userRepository.saveUser(testUser) } returns testUser
        coJustRun { userCache.putUser(testUser) }
        coEvery { userCache.getUser("1") } returns testUser

        val savedResult = userManager.saveUser(testUser)
        val getResult = userManager.getUser("1")

        assertThat(savedResult).isEqualTo(testUser)
        assertThat(getResult).isEqualTo(testUser)
        coVerify { userRepository.saveUser(testUser) }
        coVerify { userCache.getUser("1") }
    }

    @Test
    fun `deleteUser followed by getUser should return null`() = runTest(testDispatcher) {
        coEvery { userRepository.deleteUser("1") } returns true
        coJustRun { userCache.removeUser("1") }
        coEvery { userCache.getUser("1") } returns null
        coEvery { userRepository.fetchUserById("1") } returns null

        val deleteResult = userManager.deleteUser("1")
        val getResult = userManager.getUser("1")

        assertThat(deleteResult).isTrue()
        assertThat(getResult).isNull()
        coVerify { userRepository.deleteUser("1") }
        coVerify { userCache.removeUser("1") }
        coVerify { userCache.getUser("1") }
        coVerify { userRepository.fetchUserById("1") }
    }
}