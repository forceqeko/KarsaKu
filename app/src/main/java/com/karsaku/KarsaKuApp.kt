package com.karsaku

import android.app.Application
import com.karsaku.di.AppContainer
import com.karsaku.notification.NotificationHelper

class KarsaKuApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        NotificationHelper.createNotificationChannel(this)
    }
}
