package com.karsaku.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.karsaku.KarsaKuApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val pendingResult = goAsync()
            val app = context.applicationContext as KarsaKuApp

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    app.container.reminderRepository.rescheduleAllActiveAlarms()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
