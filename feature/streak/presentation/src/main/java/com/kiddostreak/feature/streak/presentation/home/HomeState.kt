package com.kiddostreak.feature.streak.presentation.home

import androidx.compose.runtime.Stable
import com.kiddostreak.feature.streak.presentation.model.StreakWithStatsUi

@Stable
data class HomeState(
    val streaks: List<StreakWithStatsUi> = emptyList(),
    val isLoading: Boolean = true,
    val todayFormatted: String = "",
) {
    val completedCount: Int get() = streaks.count { it.isCompletedToday }
}
