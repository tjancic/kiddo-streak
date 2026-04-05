package com.kiddostreak.feature.streak.domain.model

data class StreakStats(
    val currentStreak: Int,
    val longestStreak: Int,
    val totalCompletions: Int,
    val completionRate: Float,
    val currentMilestone: Milestone?,
    val nextMilestone: Milestone?
)
