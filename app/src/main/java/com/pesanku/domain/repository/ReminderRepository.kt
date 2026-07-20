package com.pesanku.domain.repository

import com.pesanku.domain.model.Reminder
import com.pesanku.domain.model.Settings
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun getAllReminders(): Flow<List<Reminder>>
    fun getActiveReminders(): Flow<List<Reminder>>
    suspend fun getReminderById(id: Int): Reminder?
    suspend fun addReminder(reminder: Reminder): Long
    suspend fun updateReminder(reminder: Reminder)
    suspend fun deleteReminder(reminder: Reminder)
    suspend fun toggleReminderActive(reminderId: Int, isActive: Boolean)
    suspend fun rescheduleAllActiveAlarms()

    // Settings
    fun getSettings(): Flow<Settings>
    suspend fun updateDefaultSound(enabled: Boolean)
    suspend fun updateDefaultVibration(enabled: Boolean)
    suspend fun updateSnoozeDuration(minutes: Int)
    suspend fun updateThemeMode(theme: com.pesanku.domain.model.AppTheme)
}
