package com.pesanku

import android.app.Application
import com.pesanku.di.AppContainer
import com.pesanku.notification.NotificationHelper

class PesanKuApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        NotificationHelper.createNotificationChannel(this)
    }
}
