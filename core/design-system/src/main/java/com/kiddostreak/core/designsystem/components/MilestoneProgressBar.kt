package com.kiddostreak.core.designsystem.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kiddostreak.core.designsystem.theme.KiddoStreakTheme
import com.kiddostreak.core.designsystem.theme.StreakOrange

@Composable
fun MilestoneProgressBar(
    currentDays: Int,
    streakColor: Color,
    nextMilestoneDays: Int?,
    modifier: Modifier = Modifier,
) {
    val rawProgress = if (nextMilestoneDays != null && nextMilestoneDays > 0) {
        currentDays.toFloat() / nextMilestoneDays
    } else {
        1f
    }
    val progress by animateFloatAsState(
        targetValue = rawProgress.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "milestone_progress",
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {},
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (nextMilestoneDays != null) "$currentDays / $nextMilestoneDays days" else "$currentDays days",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Next milestone",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = streakColor,
            trackColor = streakColor.copy(alpha = 0.15f),
            strokeCap = StrokeCap.Round,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MilestoneProgressBarPreview() {
    KiddoStreakTheme(useDynamicColors = false) {
        MilestoneProgressBar(
            currentDays = 5,
            streakColor = StreakOrange,
            nextMilestoneDays = 7,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MilestoneProgressBarHighPreview() {
    KiddoStreakTheme(useDynamicColors = false) {
        MilestoneProgressBar(
            currentDays = 25,
            streakColor = StreakOrange,
            nextMilestoneDays = 30,
            modifier = Modifier.padding(16.dp),
        )
    }
}
