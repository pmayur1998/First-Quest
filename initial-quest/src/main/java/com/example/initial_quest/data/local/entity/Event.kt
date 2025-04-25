package com.example.initial_quest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class Event(
    @PrimaryKey
    val id: Int,
    val title: String,
    val body: String,
    val userId: Int
)