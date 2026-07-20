package com.pesanku.alarm

import com.pesanku.domain.model.Reminder

interface AlarmScheduler {
    fun schedule(reminder: Reminder)
    fun cancel(reminderId: Int)
}
