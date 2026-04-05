package com.kiddostreak.feature.streak.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiddostreak.core.designsystem.components.FlameIcon
import com.kiddostreak.core.designsystem.components.FlameLevel
import com.kiddostreak.core.designsystem.components.KiddoStreakCard
import com.kiddostreak.core.designsystem.components.MilestoneProgressBar
import com.kiddostreak.core.designsystem.components.StreakCheckCircle
import com.kiddostreak.core.designsystem.components.parseHexColor
import androidx.compose.runtime.remember
import com.kiddostreak.feature.streak.presentation.R
import com.kiddostreak.feature.streak.presentation.model.StreakWithStatsUi

@Composable
fun StreakListItem(
    streak: StreakWithStatsUi,
    onTap: () -> Unit,
    onCheckChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val accentColor = remember(streak.colorHex) { parseHexColor(streak.colorHex) }

    KiddoStreakCard(
        accentColor = accentColor,
        onClick = onTap,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Emoji
                Text(
                    text = streak.emoji,
                    fontSize = 32.sp,
                    modifier = Modifier.size(40.dp),
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Name + streak count
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = streak.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(
                                R.string.home_streak_day_count,
                                streak.currentStreak,
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        if (streak.flameLevel != FlameLevel.NONE) {
                            Spacer(modifier = Modifier.width(4.dp))
                            FlameIcon(
                                level = streak.flameLevel,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Check circle
                StreakCheckCircle(
                    checked = streak.isCompletedToday,
                    streakColor = accentColor,
                    onCheckedChange = onCheckChange,
                    size = 44.dp,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Milestone progress
            MilestoneProgressBar(
                currentDays = streak.currentStreak,
                streakColor = accentColor,
                nextMilestoneDays = streak.nextMilestoneDays,
            )
        }
    }
}
