package com.pesanku.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.pesanku.PesanKuApp
import com.pesanku.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AlarmSchedulerImpl.ACTION_TRIGGER_ALARM) {
            val reminderId = intent.getIntExtra(AlarmSchedulerImpl.EXTRA_REMINDER_ID, -1)
            val title = intent.getStringExtra(AlarmSchedulerImpl.EXTRA_REMINDER_TITLE) ?: "Pengingat"
            val message = intent.getStringExtra(AlarmSchedulerImpl.EXTRA_REMINDER_MESSAGE) ?: ""
            val soundEnabled = intent.getBooleanExtra(AlarmSchedulerImpl.EXTRA_SOUND_ENABLED, true)
            val vibrationEnabled = intent.getBooleanExtra(AlarmSchedulerImpl.EXTRA_VIBRATION_ENABLED, true)

            // 1. Show notification with full-screen intent fallback
            NotificationHelper.showAlarmNotification(
                context = context,
                reminderId = reminderId,
                title = title,
                message = message,
                soundEnabled = soundEnabled,
                vibrationEnabled = vibrationEnabled
            )

            // 2. Direct Activity launch to immediately bring AlarmActivity to foreground
            val alarmIntent = Intent(context, com.pesanku.ui.alarm.AlarmActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra(AlarmSchedulerImpl.EXTRA_REMINDER_ID, reminderId)
                putExtra(AlarmSchedulerImpl.EXTRA_REMINDER_TITLE, title)
                putExtra(AlarmSchedulerImpl.EXTRA_REMINDER_MESSAGE, message)
                putExtra(AlarmSchedulerImpl.EXTRA_SOUND_ENABLED, soundEnabled)
                putExtra(AlarmSchedulerImpl.EXTRA_VIBRATION_ENABLED, vibrationEnabled)
            }
            try {
                context.startActivity(alarmIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Handle recurring vs one-time update in background
            if (reminderId != -1) {
                val pendingResult = goAsync()
                val app = context.applicationContext as PesanKuApp
                val repository = app.container.reminderRepository

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val reminder = repository.getReminderById(reminderId)
                        if (reminder != null) {
                            if (reminder.isRecurring) {
                                // Schedule next occurrence
                                app.container.alarmScheduler.schedule(reminder)
                            } else {
                                // Mark one-time reminder as completed / inactive
                                repository.toggleReminderActive(reminderId, false)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        }
    }
}
