package com.pesanku

import android.app.Application
import com.pesanku.di.AppContainer
import com.pesanku.notification.NotificationHelper
import com.pesanku.service.ReminderForegroundService

class PesanKuApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        NotificationHelper.createNotificationChannel(this)

        // Start the foreground service to keep the process alive
        // so AlarmManager PendingIntents survive recent app removal
        ReminderForegroundService.start(this)
    }
}
