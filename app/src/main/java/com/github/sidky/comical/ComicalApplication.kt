package com.github.sidky.comical

import android.app.Application
import timber.log.Timber

class ComicalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        setupTimber()
    }

    fun setupTimber() {
        Timber.plant(Timber.DebugTree())
    }

}