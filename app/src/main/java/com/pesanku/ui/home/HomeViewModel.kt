package com.pesanku.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pesanku.domain.model.Reminder
import com.pesanku.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class HomeUiState(
    val reminders: List<Reminder> = emptyList(),
    val isLoading: Boolean = false,
    val greeting: String = "Selamat Datang"
)

class HomeViewModel(
    private val repository: ReminderRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = repository.getAllReminders()
        .map { reminders ->
            HomeUiState(
                reminders = reminders,
                isLoading = false,
                greeting = getGreetingByTime()
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState(isLoading = true)
        )

    fun toggleActive(reminder: Reminder, isActive: Boolean) {
        viewModelScope.launch {
            repository.toggleReminderActive(reminder.id, isActive)
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.deleteReminder(reminder)
        }
    }

    private fun getGreetingByTime(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 4..10 -> "Selamat Pagi 🌅"
            in 11..14 -> "Selamat Siang ☀️"
            in 15..18 -> "Selamat Sore 🌇"
            else -> "Selamat Malam 🌙"
        }
    }

    class Factory(private val repository: ReminderRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }
}
