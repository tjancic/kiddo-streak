package com.kiddostreak.feature.streak.presentation.mapper

import com.kiddostreak.feature.streak.domain.model.Milestone
import com.kiddostreak.feature.streak.domain.model.StreakStats
import com.kiddostreak.feature.streak.domain.model.StreakWithStats
import com.kiddostreak.core.designsystem.components.FlameLevel
import com.kiddostreak.feature.streak.presentation.model.StreakStatsUi
import com.kiddostreak.feature.streak.presentation.model.StreakWithStatsUi
import kotlin.math.roundToInt

fun StreakWithStats.toUi(): StreakWithStatsUi {
    val nextMilestoneVal = stats.nextMilestone
    val milestoneProgress = if (nextMilestoneVal != null) {
        val previousMilestoneDays = stats.currentMilestone?.days ?: 0
        val range = nextMilestoneVal.days - previousMilestoneDays
        if (range > 0) {
            ((stats.currentStreak - previousMilestoneDays).toFloat() / range).coerceIn(0f, 1f)
        } else {
            0f
        }
    } else {
        1f
    }

    return StreakWithStatsUi(
        id = streak.id,
        name = streak.name,
        emoji = streak.emoji,
        colorHex = streak.colorHex,
        currentStreak = stats.currentStreak,
        isCompletedToday = isCompletedToday,
        milestoneProgress = milestoneProgress,
        nextMilestoneDays = nextMilestoneVal?.days,
        flameLevel = stats.currentStreak.toFlameLevel(),
    )
}

fun StreakStats.toUi(): StreakStatsUi {
    val nextMilestoneVal = nextMilestone
    return StreakStatsUi(
        currentStreak = "$currentStreak",
        longestStreak = "$longestStreak",
        totalCompletions = "$totalCompletions",
        completionRate = "${(completionRate * 100).roundToInt()}%",
        currentMilestone = currentMilestone?.toDisplayName(),
        nextMilestone = nextMilestoneVal?.toDisplayName(),
        nextMilestoneDays = nextMilestoneVal?.days,
        milestoneProgress = if (nextMilestoneVal != null) {
            val previousDays = currentMilestone?.days ?: 0
            val range = nextMilestoneVal.days - previousDays
            if (range > 0) {
                ((currentStreak - previousDays).toFloat() / range).coerceIn(0f, 1f)
            } else {
                0f
            }
        } else {
            1f
        },
    )
}

fun Milestone.toDisplayName(): String {
    return when (this) {
        Milestone.WEEK -> "1 Week"
        Milestone.TWO_WEEKS -> "2 Weeks"
        Milestone.MONTH -> "1 Month"
        Milestone.QUARTER -> "3 Months"
        Milestone.HUNDRED -> "100 Days"
        Milestone.YEAR -> "1 Year"
    }
}

private fun Int.toFlameLevel(): FlameLevel {
    return when {
        this <= 0 -> FlameLevel.NONE
        this < 7 -> FlameLevel.WARM
        this < 30 -> FlameLevel.HOT
        else -> FlameLevel.BLAZING
    }
}
