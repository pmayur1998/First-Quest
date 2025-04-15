package com.example.first_quest.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.first_quest.ui.eventdetail.EventDetailScreen
import com.example.first_quest.ui.eventlist.EventListScreen

@Composable
fun EventNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController, startDestination = Screen.EventList.route
    ) {
        animatedComposable(route = Screen.EventList.route) {
            EventListScreen(onEventClick = { eventId ->
                navController.navigate(
                    Screen.EventDetail.createRoute(
                        eventId
                    )
                )
            })
        }

        animatedComposable(
            route = Screen.EventDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.IntType })
        ) {
            EventDetailScreen(onBackClick = { navController.popBackStack() })
        }
    }
}

sealed class Screen(val route: String) {
    data object EventList : Screen("event_list")
    data object EventDetail : Screen("event_detail/{eventId}") {
        fun createRoute(eventId: Int) = "event_detail/$eventId"
    }
}

private fun NavGraphBuilder.animatedComposable(
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