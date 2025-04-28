package com.example.common.utils

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
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

fun NavGraphBuilder.animatedComposable(
    route: String, arguments: List<NamedNavArgument> = emptyList(), content: @Composable () -> Unit
) {
    composable(
        route = route,
        enterTransition = { slideIntoContainer(SlideDirection.Start, tween(300)) },
        exitTransition = { slideOutOfContainer(SlideDirection.Start, tween(300)) },
        popEnterTransition = { slideIntoContainer(SlideDirection.End, tween(300)) },
        popExitTransition = { slideOutOfContainer(SlideDirection.End, tween(300)) },
        arguments = arguments,
        content = { content() },
    )
}