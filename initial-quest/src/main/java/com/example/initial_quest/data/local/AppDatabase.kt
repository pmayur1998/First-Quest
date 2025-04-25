package com.example.initial_quest.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.initial_quest.data.local.dao.EventDao
import com.example.initial_quest.data.local.entity.Event

@Database(entities = [Event::class], version = 1, exportSchema = false)
abstract class EventDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}