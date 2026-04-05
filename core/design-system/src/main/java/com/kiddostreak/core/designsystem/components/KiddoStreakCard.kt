package com.kiddostreak.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kiddostreak.core.designsystem.theme.KiddoStreakTheme
import com.kiddostreak.core.designsystem.theme.StreakOrange

@Composable
fun KiddoStreakCard(
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    accentWidth: Dp = 4.dp,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            CardContent(
                accentColor = accentColor,
                accentWidth = accentWidth,
                content = content,
            )
        }
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            CardContent(
                accentColor = accentColor,
                accentWidth = accentWidth,
                content = content,
            )
        }
    }
}

@Composable
private fun CardContent(
    accentColor: Color,
    accentWidth: Dp,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier.height(IntrinsicSize.Min),
    ) {
        // Colored accent strip on the left
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(accentWidth)
                .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                .background(accentColor),
        )

        // Actual content with padding
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp),
        ) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun KiddoStreakCardPreview() {
    KiddoStreakTheme(useDynamicColors = false) {
        KiddoStreakCard(
            accentColor = StreakOrange,
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Read for 20 minutes",
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun KiddoStreakCardClickablePreview() {
    KiddoStreakTheme(useDynamicColors = false) {
        KiddoStreakCard(
            accentColor = StreakOrange,
            onClick = {},
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Clickable card content",
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}
