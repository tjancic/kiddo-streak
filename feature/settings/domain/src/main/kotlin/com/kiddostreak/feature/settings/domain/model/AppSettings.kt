package com.kiddostreak.feature.settings.domain.model

import com.kiddostreak.feature.streak.domain.model.ReminderInterval

data class AppSettings(
    val globalRemindersEnabled: Boolean = true,
    val defaultReminderInterval: ReminderInterval = ReminderInterval.Periodic(4),
    val useDynamicColors: Boolean = true,
    val showCompletedStreaks: Boolean = true,
)
