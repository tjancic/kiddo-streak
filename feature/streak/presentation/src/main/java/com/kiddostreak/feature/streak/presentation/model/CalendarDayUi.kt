package com.kiddostreak.feature.streak.presentation.model

import androidx.compose.runtime.Stable

@Stable
data class CalendarDayUi(
    val day: Int,
    val isCompleted: Boolean,
    val isToday: Boolean,
)
