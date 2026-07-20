package com.pesanku.ui.addedit

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pesanku.domain.model.ReminderCategory
import com.pesanku.ui.components.CategoryChip
import com.pesanku.ui.components.DaySelector
import com.pesanku.util.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditReminderScreen(
    viewModel: AddEditReminderViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(viewModel.isSaved) {
        if (viewModel.isSaved) {
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (viewModel.isEditMode) "Edit Pengingat" else "Tambah Pengingat",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title Input
            OutlinedTextField(
                value = viewModel.title,
                onValueChange = { viewModel.updateTitle(it) },
                label = { Text("Judul Pengingat *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Message Input
            OutlinedTextField(
                value = viewModel.message,
                onValueChange = { viewModel.updateMessage(it) },
                label = { Text("Pesan / Catatan (Opsional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(12.dp)
            )

            // Category Selection
            Text(
                text = "Kategori",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ReminderCategory.entries.forEach { cat ->
                    CategoryChip(
                        category = cat,
                        isSelected = viewModel.category == cat,
                        onClick = { viewModel.updateCategory(cat) }
                    )
                }
            }

            // Time Picker Button
            Text(
                text = "Waktu Pengingat",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        TimePickerDialog(
                            context,
                            { _, h, m -> viewModel.updateTime(h, m) },
                            viewModel.hour,
                            viewModel.minute,
                            true
                        ).show()
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Jam: ${DateTimeUtils.formatTime(viewModel.hour, viewModel.minute)}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Ubah Waktu",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Repeat Days
            Text(
                text = "Pengulangan (Ulangi di hari)",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = if (viewModel.repeatDays.isEmpty()) "Satu kali (tanpa pengulangan)" else DateTimeUtils.formatRepeatDays(viewModel.repeatDays),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            DaySelector(
                selectedDays = viewModel.repeatDays,
                onDayToggled = { viewModel.toggleRepeatDay(it) }
            )

            // Sound & Vibration Settings
            Text(
                text = "Pengaturan Notifikasi",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🔊 Suara Alarm", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = viewModel.soundEnabled,
                    onCheckedChange = { viewModel.toggleSound(it) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📳 Getaran", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = viewModel.vibrationEnabled,
                    onCheckedChange = { viewModel.toggleVibration(it) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = { viewModel.saveReminder() },
                modifier = Modifier.fillMaxWidth(),
                enabled = viewModel.title.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                Text(
                    text = if (viewModel.isEditMode) " Simpan Perubahan" else " Buat Pengingat",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}
