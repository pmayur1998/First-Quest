package com.example.first_quest.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.first_quest.data.local.dao.EventDao
import com.example.first_quest.data.local.entity.Event

@Database(entities = [Event::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}