package com.kiddostreak.feature.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.action.actionParametersOf
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.kiddostreak.feature.streak.domain.model.Streak
import com.kiddostreak.feature.streak.domain.model.StreakCompletion
import com.kiddostreak.feature.streak.domain.model.isEligibleDay
import com.kiddostreak.feature.streak.domain.repository.StreakRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import org.koin.java.KoinJavaComponent.getKoin

private const val TAG = "StreakWidget"

enum class WidgetVariant(val maxItems: Int) {
    TINY(1),
    SMALL(1),
    MEDIUM(3),
}

class StreakWidget(private val variant: WidgetVariant) : GlanceAppWidget() {

    override val sizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        Log.d(TAG, "provideGlance variant=$variant")

        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            ?: Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)

        val streaks: List<Streak>
        val completedIds: Set<String>
        var primaryStreakDays = 0

        try {
            val repository: StreakRepository = getKoin().get()
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

            val (activeStreaks, todayCompletions) = combine(
                repository.getActiveStreaks(),
                repository.getCompletionsForDate(today),
            ) { s, c -> s to c }.first()

            streaks = activeStreaks
            completedIds = todayCompletions.map { it.streakId }.toSet()

            // Calculate current streak days for the primary (first) streak
            val primary = activeStreaks.firstOrNull()
            if (primary != null) {
                val completions = repository.getCompletionsForStreak(primary.id).first()
                primaryStreakDays = calculateCurrentStreak(primary, completions, today)
            }

            Log.d(TAG, "Loaded ${streaks.size} streaks, primaryDays=$primaryStreakDays")
        } catch (e: Exception) {
            Log.e(TAG, "FAILED to load data", e)
            provideContent {
                GlanceTheme {
                    ErrorContent(launchIntent)
                }
            }
            return
        }

        provideContent {
            GlanceTheme {
                when (variant) {
                    WidgetVariant.TINY -> TinyContent(streaks, primaryStreakDays, launchIntent)
                    WidgetVariant.SMALL -> SmallContent(streaks, completedIds, primaryStreakDays, launchIntent)
                    WidgetVariant.MEDIUM -> MediumContent(streaks, completedIds, launchIntent)
                }
            }
        }
    }
}

private fun calculateCurrentStreak(
    streak: Streak,
    completions: List<StreakCompletion>,
    today: LocalDate,
): Int {
    val completedDates = completions
        .filter { it.streakId == streak.id }
        .map { it.date }
        .toSet()

    var count = 0
    var date = if (today in completedDates) today else today.minus(1, DateTimeUnit.DAY)

    while (true) {
        if (!isEligibleDay(streak.frequency, date, streak.customDays)) {
            date = date.minus(1, DateTimeUnit.DAY)
            continue
        }
        if (date in completedDates) {
            count++
            date = date.minus(1, DateTimeUnit.DAY)
        } else {
            break
        }
    }
    return count
}

// ── 1x1 Tiny: flame + current streak day count ─────────────────────────────────

@Composable
private fun TinyContent(
    streaks: List<Streak>,
    primaryStreakDays: Int,
    launchIntent: Intent,
) {
    val primary = streaks.firstOrNull()

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.widget_bg_warm))
            .padding(4.dp)
            .clickable(actionStartActivity(launchIntent)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "\uD83D\uDD25",
            style = TextStyle(fontSize = 28.sp),
        )
        Text(
            text = "$primaryStreakDays",
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}

// ── 2x1 Small: flame + streak days + primary streak info ────────────────────────

@Composable
private fun SmallContent(
    streaks: List<Streak>,
    completedIds: Set<String>,
    primaryStreakDays: Int,
    launchIntent: Intent,
) {
    val primary = streaks.firstOrNull()
    val completed = streaks.count { it.id in completedIds }
    val total = streaks.size

    Row(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.widget_bg_warm))
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .clickable(actionStartActivity(launchIntent)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Flame + day count badge
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "\uD83D\uDD25",
                style = TextStyle(fontSize = 22.sp),
            )
            Text(
                text = "$primaryStreakDays",
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
        Spacer(modifier = GlanceModifier.width(10.dp))
        if (primary == null) {
            Text(
                text = "Tap to add a streak",
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 14.sp,
                ),
            )
        } else {
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = "${primary.emoji} ${primary.name}",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    maxLines = 1,
                )
                Spacer(modifier = GlanceModifier.height(2.dp))
                Text(
                    text = "$completed/$total done today",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 12.sp,
                    ),
                )
            }
        }
    }
}

// ── 3x2 Medium: header + streak list with clickable round checkmarks ────────────

@Composable
private fun MediumContent(
    streaks: List<Streak>,
    completedIds: Set<String>,
    launchIntent: Intent,
) {
    val completed = streaks.count { it.id in completedIds }
    val total = streaks.size
    val visibleStreaks = streaks.take(3)

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.widget_bg_warm))
            .padding(14.dp),
    ) {
        // Header - tappable to open app
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .clickable(actionStartActivity(launchIntent)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "\uD83D\uDD25",
                style = TextStyle(fontSize = 22.sp),
            )
            Spacer(modifier = GlanceModifier.width(6.dp))
            Text(
                text = "$completed/$total",
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Spacer(modifier = GlanceModifier.width(6.dp))
            Text(
                text = "done today",
                modifier = GlanceModifier.defaultWeight(),
                style = TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontSize = 13.sp,
                ),
            )
        }

        Spacer(modifier = GlanceModifier.height(10.dp))

        if (streaks.isEmpty()) {
            Text(
                text = "Tap to add your first streak!",
                modifier = GlanceModifier.clickable(actionStartActivity(launchIntent)),
                style = TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontSize = 14.sp,
                ),
            )
        } else {
            visibleStreaks.forEach { streak ->
                StreakRow(
                    streak = streak,
                    isCompleted = streak.id in completedIds,
                    launchIntent = launchIntent,
                )
                Spacer(modifier = GlanceModifier.height(6.dp))
            }
        }
    }
}

// ── Streak row: emoji + name + round clickable checkmark ────────────────────────

@Composable
private fun StreakRow(
    streak: Streak,
    isCompleted: Boolean,
    launchIntent: Intent,
) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(GlanceTheme.colors.surfaceVariant)
            .cornerRadius(14.dp)
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .clickable(actionStartActivity(launchIntent)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = streak.emoji,
            style = TextStyle(fontSize = 18.sp),
        )
        Spacer(modifier = GlanceModifier.width(8.dp))
        Text(
            text = streak.name,
            modifier = GlanceModifier.defaultWeight(),
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            ),
            maxLines = 1,
        )
        Spacer(modifier = GlanceModifier.width(6.dp))
        RoundCheckmark(
            streakId = streak.id,
            isCompleted = isCompleted,
        )
    }
}

// ── Round checkmark (clickable to complete) ─────────────────────────────────────

@Composable
private fun RoundCheckmark(
    streakId: String,
    isCompleted: Boolean,
) {
    if (isCompleted) {
        Image(
            provider = ImageProvider(R.drawable.ic_circle_checked),
            contentDescription = "Completed",
            modifier = GlanceModifier
                .size(32.dp)
                .padding(2.dp),
            colorFilter = ColorFilter.tint(GlanceTheme.colors.primary),
        )
    } else {
        Image(
            provider = ImageProvider(R.drawable.ic_circle_unchecked),
            contentDescription = "Mark complete",
            modifier = GlanceModifier
                .size(32.dp)
                .padding(2.dp)
                .clickable(
                    actionRunCallback<CompleteStreakAction>(
                        parameters = actionParametersOf(
                            CompleteStreakAction.STREAK_ID_KEY to streakId,
                            CompleteStreakAction.VARIANT_KEY to WidgetVariant.MEDIUM.name,
                        ),
                    ),
                ),
            colorFilter = ColorFilter.tint(GlanceTheme.colors.outline),
        )
    }
}

// ── Error fallback ──────────────────────────────────────────────────────────────

@Composable
private fun ErrorContent(launchIntent: Intent) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.widget_bg_warm))
            .padding(12.dp)
            .clickable(actionStartActivity(launchIntent)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "\uD83D\uDD25",
            style = TextStyle(fontSize = 28.sp),
        )
        Spacer(modifier = GlanceModifier.height(4.dp))
        Text(
            text = "Tap to open",
            style = TextStyle(
                color = GlanceTheme.colors.onSurfaceVariant,
                fontSize = 14.sp,
            ),
        )
    }
}
