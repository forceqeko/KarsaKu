package com.pesanku.di

import android.content.Context
import com.pesanku.alarm.AlarmScheduler
import com.pesanku.alarm.AlarmSchedulerImpl
import com.pesanku.data.local.PesanKuDatabase
import com.pesanku.data.preferences.SettingsDataStore
import com.pesanku.data.repository.ReminderRepositoryImpl
import com.pesanku.domain.repository.ReminderRepository

class AppContainer(private val context: Context) {

    private val database by lazy {
        PesanKuDatabase.create(context)
    }

    private val reminderDao by lazy {
        database.reminderDao()
    }

    val alarmScheduler: AlarmScheduler by lazy {
        AlarmSchedulerImpl(context)
    }

    val settingsDataStore by lazy {
        SettingsDataStore(context)
    }

    val reminderRepository: ReminderRepository by lazy {
        ReminderRepositoryImpl(
            reminderDao = reminderDao,
            alarmScheduler = alarmScheduler,
            settingsDataStore = settingsDataStore
        )
    }
}
