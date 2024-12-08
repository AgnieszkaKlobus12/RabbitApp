package com.example.rabbitapp.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class AppLifecycleObserver : LifecycleObserver {

    var foreground = true

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onEnterForeground() {
        foreground = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onEnterBackground() {
        foreground = false
    }
}
