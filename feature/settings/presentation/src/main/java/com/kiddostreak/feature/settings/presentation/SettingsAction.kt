package com.kiddostreak.feature.settings.presentation

import com.kiddostreak.feature.streak.domain.model.ReminderInterval

sealed interface SettingsAction {
    data class OnToggleReminders(val enabled: Boolean) : SettingsAction
    data class OnReminderIntervalChange(val interval: ReminderInterval) : SettingsAction
    data class OnToggleDynamicColors(val enabled: Boolean) : SettingsAction
    data class OnToggleShowCompleted(val show: Boolean) : SettingsAction
    data object OnBackClick : SettingsAction
}
