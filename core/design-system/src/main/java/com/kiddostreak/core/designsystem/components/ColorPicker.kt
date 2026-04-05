package com.kiddostreak.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kiddostreak.core.designsystem.theme.KiddoStreakTheme
import com.kiddostreak.core.designsystem.theme.StreakColors

/**
 * Parses a hex color string (e.g. "#FF6B35") into a Compose [Color].
 */
fun parseHexColor(hex: String): Color {
    val sanitized = hex.removePrefix("#")
    return Color(("FF$sanitized").toLong(16))
}

@Composable
fun ColorPicker(
    selectedColorHex: String?,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    colors: List<String> = StreakColors,
    circleSize: Dp = 40.dp,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(colors, key = { it }) { colorHex ->
            val color = remember(colorHex) { parseHexColor(colorHex) }
            val isSelected = colorHex == selectedColorHex

            Box(
                modifier = Modifier
                    .size(circleSize)
                    .clip(CircleShape)
                    .background(color = color, shape = CircleShape)
                    .then(
                        if (isSelected) {
                            Modifier.border(
                                width = 3.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = CircleShape,
                            )
                        } else {
                            Modifier
                        },
                    )
                    .clickable { onColorSelected(colorHex) }
                    .semantics {
                        role = Role.RadioButton
                        selected = isSelected
                        contentDescription = "Color $colorHex"
                    },
                contentAlignment = Alignment.Center,
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(circleSize * 0.5f),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ColorPickerPreview() {
    KiddoStreakTheme(useDynamicColors = false) {
        ColorPicker(
            selectedColorHex = "#FF6B35",
            onColorSelected = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ColorPickerNoneSelectedPreview() {
    KiddoStreakTheme(useDynamicColors = false) {
        ColorPicker(
            selectedColorHex = null,
            onColorSelected = {},
        )
    }
}
