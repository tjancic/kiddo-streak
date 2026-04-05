package com.kiddostreak.feature.streak.domain.model

data class StreakWithStats(
    val streak: Streak,
    val stats: StreakStats,
    val isCompletedToday: Boolean
)
