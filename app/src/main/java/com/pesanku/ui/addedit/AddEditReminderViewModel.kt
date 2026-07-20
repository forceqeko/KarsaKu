package com.pesanku.ui.addedit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pesanku.domain.model.Reminder
import com.pesanku.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class AddEditReminderViewModel(
    private val repository: ReminderRepository,
    private val reminderId: Int?
) : ViewModel() {

    var title by mutableStateOf("")
        private set
    var message by mutableStateOf("")
        private set
    var hour by mutableIntStateOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
        private set
    var minute by mutableIntStateOf(Calendar.getInstance().get(Calendar.MINUTE))
        private set
    var category by mutableStateOf("Pekerjaan")
        private set
    var customCategoryText by mutableStateOf("")
        private set
    var isCustomCategorySelected by mutableStateOf(false)
        private set
    var repeatDays by mutableStateOf<List<Int>>(emptyList())
        private set
    var oneTimeDate by mutableStateOf<Long?>(null)
        private set
    var soundEnabled by mutableStateOf(true)
        private set
    var vibrationEnabled by mutableStateOf(true)
        private set
    var isSaved by mutableStateOf(false)
        private set
    var isEditMode by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            val settings = repository.getSettings().first()
            soundEnabled = settings.defaultSoundEnabled
            vibrationEnabled = settings.defaultVibrationEnabled

            if (reminderId != null && reminderId != -1) {
                isEditMode = true
                val existing = repository.getReminderById(reminderId)
                if (existing != null) {
                    title = existing.title
                    message = existing.message
                    hour = existing.hour
                    minute = existing.minute
                    category = existing.category
                    repeatDays = existing.repeatDays
                    oneTimeDate = existing.oneTimeDate
                    soundEnabled = existing.soundEnabled
                    vibrationEnabled = existing.vibrationEnabled

                    val defaultCats = listOf("Pekerjaan", "Pribadi", "Kesehatan", "Belanja", "Lainnya")
                    if (!defaultCats.contains(existing.category)) {
                        isCustomCategorySelected = true
                        customCategoryText = existing.category
                    }
                }
            }
        }
    }

    fun updateTitle(value: String) { title = value }
    fun updateMessage(value: String) { message = value }
    fun updateTime(h: Int, m: Int) { hour = h; minute = m }

    fun selectPresetCategory(cat: String) {
        isCustomCategorySelected = false
        category = cat
    }

    fun selectCustomCategoryMode() {
        isCustomCategorySelected = true
        if (customCategoryText.isNotBlank()) {
            category = customCategoryText.trim()
        }
    }

    fun updateCustomCategoryText(value: String) {
        customCategoryText = value
        category = value.ifBlank { "Lainnya" }
    }

    fun updateOneTimeDate(dateMillis: Long?) { oneTimeDate = dateMillis }
    fun toggleSound(enabled: Boolean) { soundEnabled = enabled }
    fun toggleVibration(enabled: Boolean) { vibrationEnabled = enabled }

    fun toggleRepeatDay(day: Int) {
        repeatDays = if (repeatDays.contains(day)) {
            repeatDays - day
        } else {
            repeatDays + day
        }
    }

    fun saveReminder() {
        if (title.isBlank()) return

        val finalCategory = if (isCustomCategorySelected && customCategoryText.isNotBlank()) {
            customCategoryText.trim()
        } else {
            category.ifBlank { "Lainnya" }
        }

        viewModelScope.launch {
            val reminder = Reminder(
                id = reminderId ?: 0,
                title = title.trim(),
                message = message.trim(),
                hour = hour,
                minute = minute,
                category = finalCategory,
                repeatDays = repeatDays,
                oneTimeDate = if (repeatDays.isEmpty()) oneTimeDate else null,
                isActive = true,
                soundEnabled = soundEnabled,
                vibrationEnabled = vibrationEnabled
            )

            if (isEditMode) {
                repository.updateReminder(reminder)
            } else {
                repository.addReminder(reminder)
            }
            isSaved = true
        }
    }

    class Factory(
        private val repository: ReminderRepository,
        private val reminderId: Int?
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddEditReminderViewModel(repository, reminderId) as T
        }
    }
}
