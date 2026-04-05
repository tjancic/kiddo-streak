package com.kiddostreak.feature.settings.data.mapper

import com.kiddostreak.feature.settings.data.dto.AppSettingsDto
import com.kiddostreak.feature.settings.domain.model.AppSettings
import com.kiddostreak.feature.streak.domain.model.ReminderIntervalDto
import com.kiddostreak.feature.streak.domain.model.toDto
import com.kiddostreak.feature.streak.domain.model.toReminderInterval

fun AppSettingsDto.toAppSettings(): AppSettings = AppSettings(
    globalRemindersEnabled = globalRemindersEnabled,
    defaultReminderInterval = ReminderIntervalDto(
        type = reminderType,
        value = reminderValue,
        minute = reminderMinute,
    ).toReminderInterval(),
    useDynamicColors = useDynamicColors,
    showCompletedStreaks = showCompletedStreaks,
)

fun AppSettings.toDto(): AppSettingsDto {
    val reminderDto = defaultReminderInterval.toDto()
    return AppSettingsDto(
        globalRemindersEnabled = globalRemindersEnabled,
        reminderType = reminderDto.type,
        reminderValue = reminderDto.value,
        reminderMinute = reminderDto.minute,
        useDynamicColors = useDynamicColors,
        showCompletedStreaks = showCompletedStreaks,
    )
}
