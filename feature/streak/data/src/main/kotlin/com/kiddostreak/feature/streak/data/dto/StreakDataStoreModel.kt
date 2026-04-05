package com.kiddostreak.feature.streak.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class StreakDataStoreModel(
    val streaks: List<StreakDto> = emptyList(),
    val completions: List<StreakCompletionDto> = emptyList()
)
