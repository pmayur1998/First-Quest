package com.example.third_quest.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.common.utils.animatedComposable
import com.example.third_quest.ui.loyalty_points.LoyaltyPointsScreen
import com.example.third_quest.ui.stats.StatsScreen

@Composable
fun StatsNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Stats.route) {
        animatedComposable(route = Screen.Stats.route) {
            StatsScreen(onUserClick = { userId ->
                navController.navigate(
                    Screen.Loyalty.createRoute(userId)
                )
            })
        }

        animatedComposable(
            route = Screen.Loyalty.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) {
            LoyaltyPointsScreen(onBackClick = { navController.popBackStack() })
        }
    }
}

sealed class Screen(val route: String) {
    data object Stats : Screen("stats")
    data object Loyalty : Screen("loyalty/{userId}") {
        fun createRoute(userId: String) = "loyalty/$userId"
    }
}