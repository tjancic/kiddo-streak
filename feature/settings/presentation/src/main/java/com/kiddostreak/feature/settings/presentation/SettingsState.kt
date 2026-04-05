package com.kiddostreak.feature.settings.presentation

import com.kiddostreak.feature.streak.domain.model.ReminderInterval

data class SettingsState(
    val globalRemindersEnabled: Boolean = true,
    val defaultReminderInterval: ReminderInterval = ReminderInterval.Periodic(4),
    val useDynamicColors: Boolean = true,
    val showCompletedStreaks: Boolean = true,
    val isLoading: Boolean = true,
)
