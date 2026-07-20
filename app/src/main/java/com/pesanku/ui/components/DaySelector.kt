package com.pesanku.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.pesanku.util.DateTimeUtils

@Composable
fun DaySelector(
    selectedDays: List<Int>,
    onDayToggled: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val days = listOf(1, 2, 3, 4, 5, 6, 7) // 1=Mon .. 7=Sun

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        days.forEach { dayInt ->
            val isSelected = selectedDays.contains(dayInt)
            val bgColor = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
            val textColor = if (isSelected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(bgColor)
                    .clickable { onDayToggled(dayInt) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = DateTimeUtils.getDayName(dayInt).take(1),
                    style = MaterialTheme.typography.labelLarge,
                    color = textColor
                )
            }
        }
    }
}
