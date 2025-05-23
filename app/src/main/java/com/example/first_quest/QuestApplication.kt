package com.example.first_quest

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class QuestApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}