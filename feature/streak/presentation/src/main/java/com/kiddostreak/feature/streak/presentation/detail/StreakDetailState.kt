package com.kiddostreak.feature.streak.presentation.detail

import androidx.compose.runtime.Stable
import com.kiddostreak.feature.streak.presentation.model.StreakStatsUi

@Stable
data class StreakDetailState(
    val streakName: String = "",
    val streakEmoji: String = "",
    val streakColorHex: String = "#FF6B35",
    val stats: StreakStatsUi? = null,
    val completedDates: Set<java.time.LocalDate> = emptySet(),
    val isCompletedToday: Boolean = false,
    val isPrimary: Boolean = false,
    val isLoading: Boolean = true,
)
