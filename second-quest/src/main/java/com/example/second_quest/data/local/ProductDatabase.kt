package com.example.second_quest.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.second_quest.data.local.dao.ProductDao
import com.example.second_quest.data.local.entity.ProductEntity

@Database(entities = [ProductEntity::class], version = 1)
abstract class ProductDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}