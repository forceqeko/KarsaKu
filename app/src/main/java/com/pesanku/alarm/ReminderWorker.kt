package com.pesanku.alarm

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pesanku.notification.NotificationHelper

class ReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val reminderId = inputData.getInt(AlarmSchedulerImpl.EXTRA_REMINDER_ID, -1)
        val title = inputData.getString(AlarmSchedulerImpl.EXTRA_REMINDER_TITLE) ?: "Pengingat"
        val message = inputData.getString(AlarmSchedulerImpl.EXTRA_REMINDER_MESSAGE) ?: ""
        val soundEnabled = inputData.getBoolean(AlarmSchedulerImpl.EXTRA_SOUND_ENABLED, true)
        val vibrationEnabled = inputData.getBoolean(AlarmSchedulerImpl.EXTRA_VIBRATION_ENABLED, true)

        NotificationHelper.showAlarmNotification(
            context = context,
            reminderId = reminderId,
            title = title,
            message = message,
            soundEnabled = soundEnabled,
            vibrationEnabled = vibrationEnabled
        )

        return Result.success()
    }
}
