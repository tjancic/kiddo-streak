package com.kiddostreak.feature.streak.presentation.model

import androidx.compose.runtime.Stable

@Stable
data class StreakStatsUi(
    val currentStreak: String,
    val longestStreak: String,
    val totalCompletions: String,
    val completionRate: String,
    val currentMilestone: String?,
    val nextMilestone: String?,
    val nextMilestoneDays: Int?,
    val milestoneProgress: Float,
)
