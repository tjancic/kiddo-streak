package com.kiddostreak.feature.streak.presentation.home

import com.kiddostreak.core.presentation.UiText

sealed interface HomeEvent {
    data class NavigateToStreakDetail(val streakId: String) : HomeEvent
    data object NavigateToCreateStreak : HomeEvent
    data object NavigateToSettings : HomeEvent
    data class ShowMilestoneCelebration(val streakName: String, val milestone: Int) : HomeEvent
    data class ShowSnackbar(val message: UiText) : HomeEvent
    data object WidgetUpdateNeeded : HomeEvent
}
