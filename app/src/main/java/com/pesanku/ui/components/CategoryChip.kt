package com.pesanku.ui.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pesanku.ui.theme.CategoryLainnyaColor
import com.pesanku.ui.theme.CategoryPekerjaanColor
import com.pesanku.ui.theme.CategoryPribadiColor
import java.util.Locale

fun getCategoryColor(categoryName: String): Color {
    return when (categoryName.trim().lowercase(Locale.getDefault())) {
        "pekerjaan", "work" -> CategoryPekerjaanColor
        "pribadi", "personal" -> CategoryPribadiColor
        "kesehatan", "health" -> Color(0xFF00997A) // Teal Green
        "belanja", "shopping" -> Color(0xFFE67E22) // Orange
        "tugas", "task" -> Color(0xFF2980B9) // Blue
        "lainnya", "other" -> CategoryLainnyaColor
        else -> {
            val hash = categoryName.trim().hashCode()
            val colors = listOf(
                Color(0xFF5B4FCF),
                Color(0xFFE84670),
                Color(0xFF00997A),
                Color(0xFFE67E22),
                Color(0xFF8E44AD),
                Color(0xFF2980B9),
                Color(0xFFD35400)
            )
            colors[kotlin.math.abs(hash) % colors.size]
        }
    }
}

@Composable
fun CategoryChip(
    category: String,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val categoryColor = getCategoryColor(category)
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
            text = category,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}
