package com.pesanku.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.pesanku.PesanKuApp
import com.pesanku.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getIntExtra(AlarmSchedulerImpl.EXTRA_REMINDER_ID, -1)
        val title = intent.getStringExtra(AlarmSchedulerImpl.EXTRA_REMINDER_TITLE) ?: "Pengingat"
        val message = intent.getStringExtra(AlarmSchedulerImpl.EXTRA_REMINDER_MESSAGE) ?: ""
        val soundEnabled = intent.getBooleanExtra(AlarmSchedulerImpl.EXTRA_SOUND_ENABLED, true)
        val vibrationEnabled = intent.getBooleanExtra(AlarmSchedulerImpl.EXTRA_VIBRATION_ENABLED, true)

        // Acquire a partial wake lock to ensure CPU stays awake during notification dispatch
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "PesanKu::AlarmWakeLock"
        )
        wakeLock.acquire(10_000L) // 10 second timeout

        try {
            // Show heads-up notification banner
            NotificationHelper.showAlarmNotification(
                context = context,
                reminderId = reminderId,
                title = title,
                message = message,
                soundEnabled = soundEnabled,
                vibrationEnabled = vibrationEnabled
            )
        } finally {
            if (wakeLock.isHeld) {
                wakeLock.release()
            }
        }

        // Handle recurring vs one-time update in background
        if (reminderId != -1) {
            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val app = context.applicationContext as PesanKuApp
                    val repository = app.container.reminderRepository
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
