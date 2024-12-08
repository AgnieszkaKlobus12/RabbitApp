package com.example.rabbitapp

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.rabbitapp.utils.AppLifecycleObserver


class MainApplication : Application() {

    val appLifecycleObserver: AppLifecycleObserver = AppLifecycleObserver()

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)
    }
}