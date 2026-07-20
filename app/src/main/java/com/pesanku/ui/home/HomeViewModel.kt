package com.pesanku.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pesanku.domain.model.Reminder
import com.pesanku.domain.model.ReminderCategory
import com.pesanku.domain.repository.ReminderRepository
import com.pesanku.util.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class HomeUiState(
    val reminders: List<Reminder> = emptyList(),
    val filteredReminders: List<Reminder> = emptyList(),
    val selectedCategory: ReminderCategory? = null,
    val isLoading: Boolean = false,
    val greeting: String = "Selamat Datang",
    val nextReminderTime: String? = null
)

class HomeViewModel(
    private val repository: ReminderRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<ReminderCategory?>(null)

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getAllReminders(),
        _selectedCategory
    ) { reminders, selectedCat ->
        val filtered = if (selectedCat == null) {
            reminders
        } else {
            reminders.filter { it.category == selectedCat }
        }

        // Calculate next upcoming active reminder time
        val activeReminders = reminders.filter { it.isActive }
        val nextTime = activeReminders.minByOrNull { DateTimeUtils.calculateNextTriggerTime(it) }?.let {
            DateTimeUtils.formatTime(it.hour, it.minute)
        }

        HomeUiState(
            reminders = reminders,
            filteredReminders = filtered,
            selectedCategory = selectedCat,
            isLoading = false,
            greeting = getGreetingByTime(),
            nextReminderTime = nextTime
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun selectCategory(category: ReminderCategory?) {
        _selectedCategory.value = category
    }

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
