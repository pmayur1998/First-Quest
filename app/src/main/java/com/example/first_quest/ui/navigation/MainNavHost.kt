package com.example.first_quest.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.common.utils.animatedComposable
import com.example.first_quest.ui.HomeScreen
import com.example.first_quest.ui.MainViewModel

@Composable
fun MainNavHost(viewModel: MainViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val quests = viewModel.quests

    NavHost(
        navController = navController, startDestination = "home"
    ) {
        animatedComposable("home") {
            HomeScreen(
                quests = quests,
                onQuestClick = { quest ->
                    navController.navigate(quest.route)
                }
            )
        }
        quests.forEach { quest ->
            animatedComposable(quest.route) {
                quest.content()
            }
        }
    }
}
