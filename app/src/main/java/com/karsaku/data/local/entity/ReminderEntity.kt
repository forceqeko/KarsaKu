package com.karsaku.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.karsaku.domain.model.Reminder
import com.karsaku.domain.model.ReminderCategory

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String,
    val hour: Int,
    val minute: Int,
    val category: String,
    val repeatDays: String,           // CSV format "1,3,5"
    val oneTimeDate: Long?,
    val isActive: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Reminder {
        val daysList = if (repeatDays.isBlank()) {
            emptyList()
        } else {
            repeatDays.split(",").mapNotNull { it.trim().toIntOrNull() }
        }
        return Reminder(
            id = id,
            title = title,
            message = message,
            hour = hour,
            minute = minute,
            category = ReminderCategory.fromString(category),
            repeatDays = daysList,
            oneTimeDate = oneTimeDate,
            isActive = isActive,
            soundEnabled = soundEnabled,
            vibrationEnabled = vibrationEnabled,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromDomain(reminder: Reminder): ReminderEntity {
            return ReminderEntity(
                id = reminder.id,
                title = reminder.title,
                message = reminder.message,
                hour = reminder.hour,
                minute = reminder.minute,
                category = reminder.category.name,
                repeatDays = reminder.repeatDays.joinToString(","),
                oneTimeDate = reminder.oneTimeDate,
                isActive = reminder.isActive,
                soundEnabled = reminder.soundEnabled,
                vibrationEnabled = reminder.vibrationEnabled,
                createdAt = reminder.createdAt
            )
        }
    }
}
