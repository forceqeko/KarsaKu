package com.karsaku.util

import com.karsaku.domain.model.Reminder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateTimeUtils {

    /**
     * Calculates the next trigger time in epoch milliseconds for a given reminder.
     */
    fun calculateNextTriggerTime(reminder: Reminder): Long {
        val now = Calendar.getInstance()

        if (reminder.isRecurring) {
            // Find the closest upcoming day matching one of repeatDays
            // repeatDays uses Calendar format: 1=Mon, 2=Tue, 3=Wed, 4=Thu, 5=Fri, 6=Sat, 7=Sun
            // Note: java.util.Calendar DAY_OF_WEEK is 1=Sun, 2=Mon, 3=Tue, 4=Wed, 5=Thu, 6=Fri, 7=Sat
            val targetDays = reminder.repeatDays.map { toCalendarDayOfWeek(it) }

            var candidate = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, reminder.hour)
                set(Calendar.MINUTE, reminder.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // Check next 7 days for the first matching day & future time
            for (dayOffset in 0..7) {
                val testCal = (candidate.clone() as Calendar).apply {
                    add(Calendar.DAY_OF_YEAR, dayOffset)
                }
                val currentDayOfWeek = testCal.get(Calendar.DAY_OF_WEEK)
                if (targetDays.contains(currentDayOfWeek) && testCal.timeInMillis > now.timeInMillis) {
                    return testCal.timeInMillis
                }
            }

            // Fallback to 7 days from now if nothing matched
            candidate.add(Calendar.DAY_OF_YEAR, 7)
            return candidate.timeInMillis
        } else {
            // One-time alarm
            val cal = Calendar.getInstance().apply {
                reminder.oneTimeDate?.let { timeInMillis = it }
                set(Calendar.HOUR_OF_DAY, reminder.hour)
                set(Calendar.MINUTE, reminder.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // If time has passed today and no specific date set, schedule for tomorrow
            if (cal.timeInMillis <= now.timeInMillis && reminder.oneTimeDate == null) {
                cal.add(Calendar.DAY_OF_YEAR, 1)
            }

            return cal.timeInMillis
        }
    }

    /**
     * Converts custom day of week (1=Mon, 7=Sun) to java.util.Calendar DAY_OF_WEEK (1=Sun, 2=Mon...7=Sat)
     */
    private fun toCalendarDayOfWeek(customDay: Int): Int {
        return when (customDay) {
            1 -> Calendar.MONDAY
            2 -> Calendar.TUESDAY
            3 -> Calendar.WEDNESDAY
            4 -> Calendar.THURSDAY
            5 -> Calendar.FRIDAY
            6 -> Calendar.SATURDAY
            7 -> Calendar.SUNDAY
            else -> Calendar.MONDAY
        }
    }

    fun formatTime(hour: Int, minute: Int): String {
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
    }

    fun formatDate(timeInMillis: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        return sdf.format(Date(timeInMillis))
    }

    fun getDayName(customDay: Int): String {
        return when (customDay) {
            1 -> "Sen"
            2 -> "Sel"
            3 -> "Rab"
            4 -> "Kam"
            5 -> "Jum"
            6 -> "Sab"
            7 -> "Min"
            else -> ""
        }
    }

    fun formatRepeatDays(repeatDays: List<Int>): String {
        if (repeatDays.isEmpty()) return "Satu kali"
        if (repeatDays.size == 7) return "Setiap Hari"
        if (repeatDays.containsAll(listOf(1, 2, 3, 4, 5)) && repeatDays.size == 5) return "Hari Kerja"
        if (repeatDays.containsAll(listOf(6, 7)) && repeatDays.size == 2) return "Akhir Pekan"

        return repeatDays.sorted().joinToString(" ") { getDayName(it) }
    }
}
