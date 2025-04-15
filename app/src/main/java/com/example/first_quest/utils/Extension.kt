package com.example.first_quest.utils

import kotlin.coroutines.cancellation.CancellationException

suspend fun <Receiver, ResponseType> Receiver.runSuspendCatching(
    requestBuilder: suspend Receiver.() -> ResponseType
): Result<ResponseType> {
    return try {
        Result.success(requestBuilder())
    } catch (cancellationException: CancellationException) {
        throw cancellationException
    } catch (generalException: Exception) {
        Result.failure(generalException)
    }
}