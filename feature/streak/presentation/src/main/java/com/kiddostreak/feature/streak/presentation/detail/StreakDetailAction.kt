package com.kiddostreak.feature.streak.presentation.detail

sealed interface StreakDetailAction {
    data object OnCheckInClick : StreakDetailAction
    data object OnEditClick : StreakDetailAction
    data object OnArchiveClick : StreakDetailAction
    data object OnSetAsPrimaryClick : StreakDetailAction
    data object OnBackClick : StreakDetailAction
}
