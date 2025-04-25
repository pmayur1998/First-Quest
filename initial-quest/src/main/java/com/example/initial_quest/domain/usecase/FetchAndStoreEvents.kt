package com.example.initial_quest.domain.usecase

import android.database.sqlite.SQLiteException
import com.example.common.domain.model.Result
import com.example.initial_quest.domain.repository.EventRepository
import com.example.common.utils.ConnectivityChecker
import com.example.common.utils.runSuspendCatching
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class FetchAndStoreEvents @Inject constructor(
    private val eventRepository: EventRepository,
    private val connectivityChecker: ConnectivityChecker
) {
    suspend operator fun invoke(): Result<Unit> {
        if (!connectivityChecker.isNetworkAvailable()) {
            return Result.Error(Throwable("No internet connection"))
        }

        return runSuspendCatching {
            eventRepository.fetchAndStoreEvents()
        }.fold(onSuccess = { Result.Success(Unit) }, onFailure = { e ->
            val message = when (e) {
                is HttpException -> when (e.code()) {
                    in 400..499 -> "Server cannot process request"
                    in 500..599 -> "Server error, please try again later"
                    else -> "Network error, please try again"
                }

                is IOException -> "Network error, please check your connection"
                is SQLiteException -> "Database error"
                else -> "Failed to refresh events"
            }
            return Result.Error(Throwable(message, e))
        })
    }
}