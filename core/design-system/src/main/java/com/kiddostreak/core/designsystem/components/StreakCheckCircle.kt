package com.kiddostreak.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kiddostreak.core.designsystem.theme.KiddoStreakTheme
import com.kiddostreak.core.designsystem.theme.StreakOrange

@Composable
fun StreakCheckCircle(
    checked: Boolean,
    streakColor: Color,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    contentDescription: String = "Complete streak",
) {
    val scale by animateFloatAsState(
        targetValue = if (checked) 1f else 0.85f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "check_scale",
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (checked) streakColor else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "check_bg_color",
    )

    val borderColor by animateColorAsState(
        targetValue = if (checked) streakColor else streakColor.copy(alpha = 0.5f),
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "check_border_color",
    )

    val checkAlpha by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "check_alpha",
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .clip(CircleShape)
            .background(color = backgroundColor, shape = CircleShape)
            .border(width = 2.dp, color = borderColor, shape = CircleShape)
            .clickable(
                onClickLabel = contentDescription,
                onClick = { onCheckedChange(!checked) },
            )
            .semantics {
                role = Role.Checkbox
                toggleableState = ToggleableState(checked)
            },
        contentAlignment = Alignment.Center,
    ) {
        if (checkAlpha > 0f) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = Color.White.copy(alpha = checkAlpha),
                modifier = Modifier.size(size * 0.55f),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StreakCheckCircleUncheckedPreview() {
    KiddoStreakTheme(useDynamicColors = false) {
        StreakCheckCircle(
            checked = false,
            streakColor = StreakOrange,
            onCheckedChange = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StreakCheckCircleCheckedPreview() {
    KiddoStreakTheme(useDynamicColors = false) {
        StreakCheckCircle(
            checked = true,
            streakColor = StreakOrange,
            onCheckedChange = {},
        )
    }
}
