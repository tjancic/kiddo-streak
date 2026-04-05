package com.kiddostreak.feature.settings.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class AppSettingsDto(
    val globalRemindersEnabled: Boolean = true,
    val reminderType: String = "periodic",
    val reminderValue: Int = 4,
    val reminderMinute: Int = 0,
    val useDynamicColors: Boolean = true,
    val showCompletedStreaks: Boolean = true,
)
