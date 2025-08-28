package com.example.tqqqwidget

import android.app.Application
import com.example.tqqqwidget.work.TqqqUpdateWorker

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        TqqqUpdateWorker.schedule(this)
    }
}
