package com.karsaku.alarm

import com.karsaku.domain.model.Reminder

interface AlarmScheduler {
    fun schedule(reminder: Reminder)
    fun cancel(reminderId: Int)
}
