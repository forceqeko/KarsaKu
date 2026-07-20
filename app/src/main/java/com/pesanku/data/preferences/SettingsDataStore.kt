package com.pesanku.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pesanku.domain.model.AppTheme
import com.pesanku.domain.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "pesanku_settings")

class SettingsDataStore(private val context: Context) {

    private object Keys {
        val DEFAULT_SOUND = booleanPreferencesKey("default_sound")
        val DEFAULT_VIBRATION = booleanPreferencesKey("default_vibration")
        val SNOOZE_DURATION = intPreferencesKey("snooze_duration")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val settingsFlow: Flow<Settings> = context.dataStore.data.map { prefs ->
        Settings(
            defaultSoundEnabled = prefs[Keys.DEFAULT_SOUND] ?: true,
            defaultVibrationEnabled = prefs[Keys.DEFAULT_VIBRATION] ?: true,
            snoozeDurationMinutes = prefs[Keys.SNOOZE_DURATION] ?: 5,
            themeMode = try {
                AppTheme.valueOf(prefs[Keys.THEME_MODE] ?: AppTheme.SYSTEM.name)
            } catch (e: Exception) {
                AppTheme.SYSTEM
            }
        )
    }

    suspend fun updateDefaultSound(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DEFAULT_SOUND] = enabled
        }
    }

    suspend fun updateDefaultVibration(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DEFAULT_VIBRATION] = enabled
        }
    }

    suspend fun updateSnoozeDuration(minutes: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SNOOZE_DURATION] = minutes
        }
    }

    suspend fun updateThemeMode(theme: AppTheme) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = theme.name
        }
    }
}
