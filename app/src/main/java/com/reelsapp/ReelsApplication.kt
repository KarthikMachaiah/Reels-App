package com.reelsapp

import android.app.Application
import com.airbnb.mvrx.Mavericks
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
open class ReelsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Mavericks.initialize(this)
    }
}
