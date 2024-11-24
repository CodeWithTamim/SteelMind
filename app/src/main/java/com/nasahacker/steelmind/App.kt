package com.nasahacker.steelmind

import android.app.Application
import android.content.Context
import com.google.android.material.color.DynamicColors
import com.tencent.mmkv.MMKV

class App : Application() {
    companion object {
        lateinit var application: App
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        application = this
    }

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(application)
        DynamicColors.applyToActivitiesIfAvailable(application)
    }
}