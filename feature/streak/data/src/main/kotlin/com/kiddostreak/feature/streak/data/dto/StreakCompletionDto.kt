package com.kiddostreak.feature.streak.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class StreakCompletionDto(
    val streakId: String,
    val date: String,
    val completedAt: Long
)
