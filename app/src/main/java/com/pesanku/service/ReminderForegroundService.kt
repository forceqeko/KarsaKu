package com.pesanku.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.pesanku.MainActivity
import com.pesanku.PesanKuApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Foreground Service yang menjaga proses aplikasi tetap hidup
 * sehingga AlarmManager PendingIntent tidak dibatalkan oleh sistem
 * saat aplikasi dikeluarkan dari recent apps.
 *
 * Ini adalah pendekatan standar yang digunakan oleh aplikasi alarm/pengingat
 * profesional di Android.
 */
class ReminderForegroundService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        const val CHANNEL_ID = "pesanku_service_channel"
        const val CHANNEL_NAME = "PesanKu Layanan Latar Belakang"
        const val NOTIFICATION_ID = 1

        fun start(context: Context) {
            val intent = Intent(context, ReminderForegroundService::class.java)
            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, ReminderForegroundService::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createServiceNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildServiceNotification()
        startForeground(NOTIFICATION_ID, notification)

        // Reschedule all active alarms every time the service starts
        // to ensure nothing is lost
        rescheduleAlarms()

        // START_STICKY ensures the system restarts this service if it gets killed
        return START_STICKY
    }

    override fun onBind(intent: IBinder?): IBinder? = null

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    /**
     * Called when the system is about to remove the task (swipe from recents).
     * We reschedule all alarms and restart the service.
     */
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        rescheduleAlarms()
        // Restart self to keep the service alive
        val restartIntent = Intent(applicationContext, ReminderForegroundService::class.java)
        applicationContext.startForegroundService(restartIntent)
    }

    private fun rescheduleAlarms() {
        serviceScope.launch {
            try {
                val app = applicationContext as PesanKuApp
                app.container.reminderRepository.rescheduleAllActiveAlarms()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createServiceNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW  // Low = no sound, minimal visual
        ).apply {
            description = "Menjaga pengingat tetap aktif di latar belakang"
            setShowBadge(false)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun buildServiceNotification(): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("PesanKu sedang aktif")
            .setContentText("Pengingat Anda berjalan di latar belakang")
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }
}
