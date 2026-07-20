package com.pesanku.domain.model

data class Reminder(
    val id: Int = 0,
    val title: String,
    val message: String,
    val hour: Int,
    val minute: Int,
    val category: ReminderCategory = ReminderCategory.LAINNYA,
    val repeatDays: List<Int> = emptyList(), // 1 = Mon, 7 = Sun. Empty = one-time
    val oneTimeDate: Long? = null,           // epoch millis for specific date
    val isActive: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    val isRecurring: Boolean
        get() = repeatDays.isNotEmpty()
}
