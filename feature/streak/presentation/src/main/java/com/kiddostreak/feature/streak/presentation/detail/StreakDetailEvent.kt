package com.kiddostreak.feature.streak.presentation.detail

import com.kiddostreak.core.presentation.UiText

sealed interface StreakDetailEvent {
    data object NavigateBack : StreakDetailEvent
    data class NavigateToEdit(val streakId: String) : StreakDetailEvent
    data class ShowSnackbar(val message: UiText) : StreakDetailEvent
    data object WidgetUpdateNeeded : StreakDetailEvent
}
