package com.example.first_quest.model

import androidx.compose.runtime.Composable

data class Quest(
    val name: String,
    val route: String,
    val content: @Composable () -> Unit
)