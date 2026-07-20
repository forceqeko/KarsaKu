package com.karsaku.ui.alarm

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.karsaku.KarsaKuApp
import com.karsaku.alarm.AlarmSchedulerImpl
import com.karsaku.domain.model.Reminder
import com.karsaku.ui.theme.KarsaKuTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmActivity : ComponentActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private val handler = Handler(Looper.getMainLooper())
    private val autoDismissRunnable = Runnable { dismissAlarm() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Turn screen on & show on lock screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? android.app.KeyguardManager
            keyguardManager?.requestDismissKeyguard(this, null)
        }
        @Suppress("DEPRECATION")
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        )

        val reminderId = intent.getIntExtra(AlarmSchedulerImpl.EXTRA_REMINDER_ID, -1)
        val title = intent.getStringExtra(AlarmSchedulerImpl.EXTRA_REMINDER_TITLE) ?: "Pengingat"
        val message = intent.getStringExtra(AlarmSchedulerImpl.EXTRA_REMINDER_MESSAGE) ?: ""
        val soundEnabled = intent.getBooleanExtra(AlarmSchedulerImpl.EXTRA_SOUND_ENABLED, true)
        val vibrationEnabled = intent.getBooleanExtra(AlarmSchedulerImpl.EXTRA_VIBRATION_ENABLED, true)

        if (soundEnabled) playSound()
        if (vibrationEnabled) startVibration()

        // Auto dismiss after 60 seconds
        handler.postDelayed(autoDismissRunnable, 60000)

        setContent {
            KarsaKuTheme {
                AlarmScreen(
                    title = title,
                    message = message,
                    onDismiss = { dismissAlarm() },
                    onSnooze = { snoozeAlarm(reminderId, title, message) }
                )
            }
        }
    }

    private fun playSound() {
        try {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            mediaPlayer = MediaPlayer().apply {
                setDataSource(applicationContext, alarmUri)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                )
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startVibration() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        val pattern = longArrayOf(0, 500, 200, 500)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, 0)
        }
    }

    private fun stopMediaAndVibration() {
        handler.removeCallbacks(autoDismissRunnable)
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        vibrator?.cancel()
        vibrator = null
    }

    private fun dismissAlarm() {
        stopMediaAndVibration()
        finish()
    }

    private fun snoozeAlarm(reminderId: Int, title: String, message: String) {
        stopMediaAndVibration()
        if (reminderId != -1) {
            val app = applicationContext as KarsaKuApp
            val calendar = java.util.Calendar.getInstance().apply {
                add(java.util.Calendar.MINUTE, 5)
            }
            val snoozeReminder = Reminder(
                id = reminderId,
                title = title,
                message = message,
                hour = calendar.get(java.util.Calendar.HOUR_OF_DAY),
                minute = calendar.get(java.util.Calendar.MINUTE),
                isActive = true
            )
            app.container.alarmScheduler.schedule(snoozeReminder)
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMediaAndVibration()
    }
}
