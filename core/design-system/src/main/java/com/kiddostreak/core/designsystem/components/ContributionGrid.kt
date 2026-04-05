package com.kiddostreak.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kiddostreak.core.designsystem.theme.KiddoStreakTheme
import com.kiddostreak.core.designsystem.theme.StreakTeal
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

/**
 * A GitHub-style contribution heatmap showing completion history.
 *
 * @param completedDates The set of dates that were completed.
 * @param streakColor The color used to fill completed day cells.
 * @param weeks Number of weeks to display (defaults to 20, roughly 5 months).
 * @param cellSize Size of each day cell.
 * @param cellSpacing Spacing between cells.
 */
@Composable
fun ContributionGrid(
    completedDates: Set<LocalDate>,
    streakColor: Color,
    modifier: Modifier = Modifier,
    weeks: Int = 20,
    cellSize: Dp = 12.dp,
    cellSpacing: Dp = 2.dp,
) {
    val today = remember { LocalDate.now() }
    val gridData = remember(completedDates, weeks, today) {
        buildGridData(today = today, completedDates = completedDates, weeks = weeks)
    }

    Column(
        modifier = modifier.semantics {
            contentDescription = "Streak contribution grid showing ${completedDates.size} completed days"
        },
    ) {
        // Month labels
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Spacer for day-of-week labels column
            Spacer(modifier = Modifier.width(24.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                items(gridData.monthLabels) { label ->
                    Box(
                        modifier = Modifier.width((cellSize + cellSpacing) * label.spanWeeks),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Text(
                            text = label.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row {
            // Day-of-week labels
            Column(
                modifier = Modifier.width(24.dp),
                verticalArrangement = Arrangement.spacedBy(cellSpacing),
            ) {
                DayOfWeek.entries.forEach { dayOfWeek ->
                    val label = if (dayOfWeek.value % 2 == 1) {
                        dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault())
                    } else {
                        ""
                    }
                    Box(
                        modifier = Modifier.size(cellSize),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        )
                    }
                }
            }

            // Grid of day cells
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(cellSpacing),
            ) {
                items(gridData.weekColumns) { week ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(cellSpacing),
                    ) {
                        week.forEach { dayCell ->
                            val cellColor = when {
                                dayCell == null -> Color.Transparent
                                dayCell.isCompleted -> streakColor.copy(
                                    alpha = dayCell.intensity.coerceIn(0.3f, 1f),
                                )
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            }

                            Box(
                                modifier = Modifier
                                    .size(cellSize)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(cellColor)
                                    .semantics {
                                        if (dayCell != null) {
                                            contentDescription = "${dayCell.date}: ${
                                                if (dayCell.isCompleted) "completed" else "not completed"
                                            }"
                                        }
                                    },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Stable
private data class DayCell(
    val date: LocalDate,
    val isCompleted: Boolean,
    /** Intensity from 0 to 1 for color opacity variation. */
    val intensity: Float,
)

@Stable
private data class MonthLabel(
    val name: String,
    val spanWeeks: Int,
)

@Stable
private data class GridData(
    val weekColumns: List<List<DayCell?>>,
    val monthLabels: List<MonthLabel>,
)

private fun buildGridData(
    today: LocalDate,
    completedDates: Set<LocalDate>,
    weeks: Int,
): GridData {
    val totalDays = weeks * 7
    val startDate = today.minusDays(totalDays.toLong() - 1)
    // Align start to Monday
    val alignedStart = startDate.minusDays(
        (startDate.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong().mod(7L),
    )

    val weekColumns = mutableListOf<List<DayCell?>>()
    var current = alignedStart

    while (current <= today) {
        val week = mutableListOf<DayCell?>()
        for (dayIndex in 0 until 7) {
            val day = current.plusDays(dayIndex.toLong())
            if (day > today) {
                week.add(null)
            } else {
                val isCompleted = day in completedDates
                // Recency-based intensity: more recent = more opaque
                val daysAgo = ChronoUnit.DAYS.between(day, today).toInt()
                val intensity = if (isCompleted) {
                    1f - (daysAgo.toFloat() / totalDays * 0.5f)
                } else {
                    0f
                }
                week.add(DayCell(date = day, isCompleted = isCompleted, intensity = intensity))
            }
        }
        weekColumns.add(week)
        current = current.plusDays(7)
    }

    // Build month labels
    val monthLabels = mutableListOf<MonthLabel>()
    var currentMonth = alignedStart.month
    var weekCount = 0

    for (weekColumn in weekColumns) {
        val firstDayInWeek = weekColumn.firstOrNull { it != null }?.date ?: continue
        if (firstDayInWeek.month != currentMonth) {
            if (weekCount > 0) {
                monthLabels.add(
                    MonthLabel(
                        name = currentMonth.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        spanWeeks = weekCount,
                    ),
                )
            }
            currentMonth = firstDayInWeek.month
            weekCount = 1
        } else {
            weekCount++
        }
    }
    // Add last month
    if (weekCount > 0) {
        monthLabels.add(
            MonthLabel(
                name = currentMonth.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                spanWeeks = weekCount,
            ),
        )
    }

    return GridData(weekColumns = weekColumns, monthLabels = monthLabels)
}

@Preview(showBackground = true)
@Composable
private fun ContributionGridPreview() {
    val today = LocalDate.now()
    val sampleDates = (0..60).filter { it % 2 == 0 || it % 3 == 0 }
        .map { today.minusDays(it.toLong()) }
        .toSet()

    KiddoStreakTheme(useDynamicColors = false) {
        ContributionGrid(
            completedDates = sampleDates,
            streakColor = StreakTeal,
            modifier = Modifier.padding(16.dp),
        )
    }
}
