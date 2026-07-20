package com.karsaku.di

import android.content.Context
import com.karsaku.alarm.AlarmScheduler
import com.karsaku.alarm.AlarmSchedulerImpl
import com.karsaku.data.local.KarsaKuDatabase
import com.karsaku.data.preferences.SettingsDataStore
import com.karsaku.data.repository.ReminderRepositoryImpl
import com.karsaku.domain.repository.ReminderRepository

class AppContainer(private val context: Context) {

    private val database by lazy {
        KarsaKuDatabase.create(context)
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
