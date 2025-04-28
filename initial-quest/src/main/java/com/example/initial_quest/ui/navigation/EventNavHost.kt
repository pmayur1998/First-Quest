package com.example.initial_quest.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.common.utils.animatedComposable
import com.example.initial_quest.ui.eventdetail.EventDetailScreen
import com.example.initial_quest.ui.eventlist.EventListScreen

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

