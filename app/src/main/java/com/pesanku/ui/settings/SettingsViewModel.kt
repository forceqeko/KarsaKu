package com.pesanku.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pesanku.domain.model.AppTheme
import com.pesanku.domain.model.Settings
import com.pesanku.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: ReminderRepository
) : ViewModel() {

    val settingsState: StateFlow<Settings> = repository.getSettings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Settings()
        )

    fun toggleDefaultSound(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateDefaultSound(enabled)
        }
    }

    fun toggleDefaultVibration(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateDefaultVibration(enabled)
        }
    }

    fun updateSnoozeDuration(minutes: Int) {
        viewModelScope.launch {
            repository.updateSnoozeDuration(minutes)
        }
    }

    fun updateThemeMode(theme: AppTheme) {
        viewModelScope.launch {
            repository.updateThemeMode(theme)
        }
    }

    class Factory(private val repository: ReminderRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(repository) as T
        }
    }
}
