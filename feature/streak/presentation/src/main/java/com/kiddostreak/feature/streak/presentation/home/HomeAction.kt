package com.kiddostreak.feature.streak.presentation.home

sealed interface HomeAction {
    data class OnStreakTap(val streakId: String) : HomeAction
    data class OnStreakCheckIn(val streakId: String) : HomeAction
    data class OnStreakUncheck(val streakId: String) : HomeAction
    data object OnAddStreakClick : HomeAction
    data object OnSettingsClick : HomeAction
}
