package com.example.first_quest.ui

import androidx.lifecycle.ViewModel
import com.example.first_quest.model.Quest
import com.example.initial_quest.ui.navigation.EventNavHost
import com.example.second_quest.ui.productlist.ProductSearchScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    val quests = listOf(
        Quest(
            name = "Quest 1",
            route = "first_quest",
            content = { EventNavHost() }
        ),
        Quest(
            name = "Quest 2",
            route = "second_quest",
            content = { ProductSearchScreen() }
        )
    )
}