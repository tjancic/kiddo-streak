package com.kiddostreak.feature.streak.domain.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

data class StreakCompletion(
    val streakId: String,
    val date: LocalDate,
    val completedAt: Instant
)
