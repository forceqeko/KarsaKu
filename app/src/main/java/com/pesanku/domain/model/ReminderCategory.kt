package com.pesanku.domain.model

enum class ReminderCategory(val displayName: String) {
    PEKERJAAN("Pekerjaan"),
    PRIBADI("Pribadi"),
    LAINNYA("Lainnya");

    companion object {
        fun fromString(value: String): ReminderCategory {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: LAINNYA
        }
    }
}
