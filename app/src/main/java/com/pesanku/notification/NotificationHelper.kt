package com.pesanku.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.pesanku.alarm.AlarmSchedulerImpl
import com.pesanku.ui.alarm.AlarmActivity

object NotificationHelper {

    const val CHANNEL_ID = "pesanku_reminder_channel"
    const val CHANNEL_NAME = "Pengingat PesanKu"
    const val NOTIFICATION_ID_BASE = 1000

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi pengingat layar penuh PesanKu"
                setSound(soundUri, audioAttributes)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 400, 400, 400, 800)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showAlarmNotification(
        context: Context,
        reminderId: Int,
        title: String,
        message: String,
        soundEnabled: Boolean,
        vibrationEnabled: Boolean
    ) {
        val fullScreenIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(AlarmSchedulerImpl.EXTRA_REMINDER_ID, reminderId)
            putExtra(AlarmSchedulerImpl.EXTRA_REMINDER_TITLE, title)
            putExtra(AlarmSchedulerImpl.EXTRA_REMINDER_MESSAGE, message)
            putExtra(AlarmSchedulerImpl.EXTRA_SOUND_ENABLED, soundEnabled)
            putExtra(AlarmSchedulerImpl.EXTRA_VIBRATION_ENABLED, vibrationEnabled)
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            reminderId,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        if (soundEnabled) {
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
        } else {
            builder.setSound(null)
        }

        if (vibrationEnabled) {
            builder.setVibrate(longArrayOf(0, 400, 400, 400, 800))
        } else {
            builder.setVibrate(longArrayOf(0))
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_BASE + reminderId, builder.build())
    }
}
