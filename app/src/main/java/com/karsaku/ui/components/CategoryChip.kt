package com.karsaku.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.karsaku.domain.model.ReminderCategory
import com.karsaku.ui.theme.CategoryLainnyaColor
import com.karsaku.ui.theme.CategoryPekerjaanColor
import com.karsaku.ui.theme.CategoryPribadiColor

@Composable
fun CategoryChip(
    category: ReminderCategory,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val categoryColor = when (category) {
        ReminderCategory.PEKERJAAN -> CategoryPekerjaanColor
        ReminderCategory.PRIBADI -> CategoryPribadiColor
        ReminderCategory.LAINNYA -> CategoryLainnyaColor
    }

    val backgroundColor = if (isSelected) categoryColor else categoryColor.copy(alpha = 0.15f)
    val textColor = if (isSelected) Color.White else categoryColor

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = category.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}
