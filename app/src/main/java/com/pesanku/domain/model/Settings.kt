package com.pesanku.domain.model

enum class AppTheme {
    SYSTEM, DARK, LIGHT
}

data class Settings(
    val defaultSoundEnabled: Boolean = true,
    val defaultVibrationEnabled: Boolean = true,
    val snoozeDurationMinutes: Int = 5,
    val themeMode: AppTheme = AppTheme.SYSTEM
)
