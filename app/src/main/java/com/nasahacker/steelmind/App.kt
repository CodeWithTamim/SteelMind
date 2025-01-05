package com.nasahacker.steelmind

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.android.material.color.DynamicColors
import com.tencent.mmkv.MMKV

class App : Application() {
    companion object {
        lateinit var application: App
        private const val TAG = "[APP]"
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        application = this
        Log.d(TAG, "attachBaseContext: Application context attached")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: Application is starting...")
        MMKV.initialize(application)
        Log.d(TAG, "onCreate: MMKV initialized successfully")
        DynamicColors.applyToActivitiesIfAvailable(application)
        Log.d(TAG, "onCreate: Dynamic colors applied to activities")
    }
}
