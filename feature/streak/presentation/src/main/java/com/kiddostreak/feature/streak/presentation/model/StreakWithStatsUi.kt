package com.kiddostreak.feature.streak.presentation.model

import androidx.compose.runtime.Stable
import com.kiddostreak.core.designsystem.components.FlameLevel

@Stable
data class StreakWithStatsUi(
    val id: String,
    val name: String,
    val emoji: String,
    val colorHex: String,
    val currentStreak: Int,
    val isCompletedToday: Boolean,
    val milestoneProgress: Float,
    val nextMilestoneDays: Int?,
    val flameLevel: FlameLevel,
)
