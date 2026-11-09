package com.luna.ai

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LunaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
