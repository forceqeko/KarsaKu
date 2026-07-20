package com.karsaku.data.repository

import com.karsaku.alarm.AlarmScheduler
import com.karsaku.data.local.ReminderDao
import com.karsaku.data.local.entity.ReminderEntity
import com.karsaku.data.preferences.SettingsDataStore
import com.karsaku.domain.model.AppTheme
import com.karsaku.domain.model.Reminder
import com.karsaku.domain.model.Settings
import com.karsaku.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReminderRepositoryImpl(
    private val reminderDao: ReminderDao,
    private val alarmScheduler: AlarmScheduler,
    private val settingsDataStore: SettingsDataStore
) : ReminderRepository {

    override fun getAllReminders(): Flow<List<Reminder>> {
        return reminderDao.getAllReminders().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getActiveReminders(): Flow<List<Reminder>> {
        return reminderDao.getActiveReminders().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getReminderById(id: Int): Reminder? {
        return reminderDao.getReminderById(id)?.toDomain()
    }

    override suspend fun addReminder(reminder: Reminder): Long {
        val entity = ReminderEntity.fromDomain(reminder)
        val insertedId = reminderDao.insert(entity).toInt()
        val savedReminder = reminder.copy(id = insertedId)
        if (savedReminder.isActive) {
            alarmScheduler.schedule(savedReminder)
        }
        return insertedId.toLong()
    }

    override suspend fun updateReminder(reminder: Reminder) {
        val entity = ReminderEntity.fromDomain(reminder)
        reminderDao.update(entity)
        alarmScheduler.cancel(reminder.id)
        if (reminder.isActive) {
            alarmScheduler.schedule(reminder)
        }
    }

    override suspend fun deleteReminder(reminder: Reminder) {
        alarmScheduler.cancel(reminder.id)
        reminderDao.deleteById(reminder.id)
    }

    override suspend fun toggleReminderActive(reminderId: Int, isActive: Boolean) {
        reminderDao.updateActiveState(reminderId, isActive)
        val reminder = getReminderById(reminderId)
        if (reminder != null) {
            if (isActive) {
                alarmScheduler.schedule(reminder)
            } else {
                alarmScheduler.cancel(reminderId)
            }
        }
    }

    override suspend fun rescheduleAllActiveAlarms() {
        val activeEntities = reminderDao.getActiveRemindersList()
        activeEntities.forEach { entity ->
            val reminder = entity.toDomain()
            alarmScheduler.schedule(reminder)
        }
    }

    override fun getSettings(): Flow<Settings> = settingsDataStore.settingsFlow

    override suspend fun updateDefaultSound(enabled: Boolean) {
        settingsDataStore.updateDefaultSound(enabled)
    }

    override suspend fun updateDefaultVibration(enabled: Boolean) {
        settingsDataStore.updateDefaultVibration(enabled)
    }

    override suspend fun updateSnoozeDuration(minutes: Int) {
        settingsDataStore.updateSnoozeDuration(minutes)
    }

    override suspend fun updateThemeMode(theme: AppTheme) {
        settingsDataStore.updateThemeMode(theme)
    }
}
