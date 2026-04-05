package com.kiddostreak.core.designsystem.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kiddostreak.core.designsystem.theme.KiddoStreakTheme

@Stable
enum class FlameLevel {
    /** No streak (0 or fewer days). */
    NONE,

    /** 1-6 day streak: small, warm orange flame. */
    WARM,

    /** 7-29 day streak: medium, red-orange flame. */
    HOT,

    /** 30+ day streak: large, bright flame with subtle pulse. */
    BLAZING,
}

fun streakDaysToFlameLevel(days: Int): FlameLevel = when {
    days <= 0 -> FlameLevel.NONE
    days >= 30 -> FlameLevel.BLAZING
    days >= 7 -> FlameLevel.HOT
    else -> FlameLevel.WARM
}

@Composable
fun FlameIcon(
    level: FlameLevel,
    modifier: Modifier = Modifier,
    contentDescription: String = "Streak flame",
) {
    if (level == FlameLevel.NONE) return

    val size: Dp
    val tint: Color

    when (level) {
        FlameLevel.NONE -> return // handled above, satisfy exhaustive when
        FlameLevel.WARM -> {
            size = 24.dp
            tint = Color(0xFFFF9800) // warm orange
        }
        FlameLevel.HOT -> {
            size = 32.dp
            tint = Color(0xFFFF5722) // red-orange
        }
        FlameLevel.BLAZING -> {
            size = 40.dp
            tint = Color(0xFFFF3D00) // bright red-orange
        }
    }

    if (level == FlameLevel.BLAZING) {
        val infiniteTransition = rememberInfiniteTransition(label = "flame_pulse")
        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.12f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 800, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "flame_scale",
        )

        Icon(
            imageVector = Icons.Filled.Whatshot,
            contentDescription = contentDescription,
            tint = tint,
            modifier = modifier
                .size(size)
                .scale(pulseScale),
        )
    } else {
        Icon(
            imageVector = Icons.Filled.Whatshot,
            contentDescription = contentDescription,
            tint = tint,
            modifier = modifier.size(size),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FlameIconWarmPreview() {
    KiddoStreakTheme(useDynamicColors = false) {
        FlameIcon(level = FlameLevel.WARM)
    }
}

@Preview(showBackground = true)
@Composable
private fun FlameIconHotPreview() {
    KiddoStreakTheme(useDynamicColors = false) {
        FlameIcon(level = FlameLevel.HOT)
    }
}

@Preview(showBackground = true)
@Composable
private fun FlameIconBlazingPreview() {
    KiddoStreakTheme(useDynamicColors = false) {
        FlameIcon(level = FlameLevel.BLAZING)
    }
}
