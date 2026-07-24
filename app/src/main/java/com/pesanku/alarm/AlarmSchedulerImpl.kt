package com.pesanku.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.pesanku.domain.model.Reminder
import com.pesanku.util.DateTimeUtils
import java.util.concurrent.TimeUnit

class AlarmSchedulerImpl(
    private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val workManager = WorkManager.getInstance(context)

    override fun schedule(reminder: Reminder) {
        if (!reminder.isActive) return

        val triggerTime = DateTimeUtils.calculateNextTriggerTime(reminder)
        val now = System.currentTimeMillis()
        val delayMillis = (triggerTime - now).coerceAtLeast(0L)

        // -------------------------------------------------------------
        // ENGINE 1: AlarmManager (Primary Alarm Clock Engine)
        // -------------------------------------------------------------
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_TRIGGER_ALARM
            putExtra(EXTRA_REMINDER_ID, reminder.id)
            putExtra(EXTRA_REMINDER_TITLE, reminder.title)
            putExtra(EXTRA_REMINDER_MESSAGE, reminder.message)
            putExtra(EXTRA_SOUND_ENABLED, reminder.soundEnabled)
            putExtra(EXTRA_VIBRATION_ENABLED, reminder.vibrationEnabled)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val showIntent = Intent(context, com.pesanku.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val showPendingIntent = PendingIntent.getActivity(
            context,
            reminder.id,
            showIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerTime, showPendingIntent)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            try {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        // -------------------------------------------------------------
        // ENGINE 2: WorkManager (Backup System Daemon Engine)
        // WorkManager is managed by JobScheduler in system_server,
        // which survives recent app swipes / task kills even on OEM ROMs.
        // -------------------------------------------------------------
        val inputData = Data.Builder()
            .putInt(EXTRA_REMINDER_ID, reminder.id)
            .putString(EXTRA_REMINDER_TITLE, reminder.title)
            .putString(EXTRA_REMINDER_MESSAGE, reminder.message)
            .putBoolean(EXTRA_SOUND_ENABLED, reminder.soundEnabled)
            .putBoolean(EXTRA_VIBRATION_ENABLED, reminder.vibrationEnabled)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        workManager.enqueueUniqueWork(
            getWorkName(reminder.id),
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    override fun cancel(reminderId: Int) {
        // Cancel Engine 1 (AlarmManager)
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_TRIGGER_ALARM
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }

        // Cancel Engine 2 (WorkManager)
        workManager.cancelUniqueWork(getWorkName(reminderId))
    }

    private fun getWorkName(reminderId: Int): String = "pesanku_reminder_work_$reminderId"

    companion object {
        const val ACTION_TRIGGER_ALARM = "com.pesanku.ACTION_TRIGGER_ALARM"
        const val EXTRA_REMINDER_ID = "extra_reminder_id"
        const val EXTRA_REMINDER_TITLE = "extra_reminder_title"
        const val EXTRA_REMINDER_MESSAGE = "extra_reminder_message"
        const val EXTRA_SOUND_ENABLED = "extra_sound_enabled"
        const val EXTRA_VIBRATION_ENABLED = "extra_vibration_enabled"
    }
}
