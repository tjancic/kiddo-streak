package com.kiddostreak.feature.streak.presentation.model

import androidx.compose.runtime.Stable

@Stable
data class CalendarMonthUi(
    val year: Int,
    val month: Int,
    val label: String,
    val days: List<CalendarDayUi>,
)
