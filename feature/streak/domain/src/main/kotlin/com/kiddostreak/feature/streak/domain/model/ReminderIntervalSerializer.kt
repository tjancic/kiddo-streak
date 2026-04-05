package com.kiddostreak.feature.streak.domain.model

data class ReminderIntervalDto(
    val type: String,
    val value: Int,
    val minute: Int,
    val fromHour: Int = 8,
    val toHour: Int = 22,
)

fun ReminderInterval.toDto(): ReminderIntervalDto = when (this) {
    is ReminderInterval.None -> ReminderIntervalDto("none", 0, 0)
    is ReminderInterval.Periodic -> ReminderIntervalDto("periodic", hours, 0, fromHour, toHour)
    is ReminderInterval.FixedTime -> ReminderIntervalDto("fixed", hour, minute)
}

fun ReminderIntervalDto.toReminderInterval(): ReminderInterval = when (type) {
    "periodic" -> ReminderInterval.Periodic(hours = value, fromHour = fromHour, toHour = toHour)
    "fixed" -> ReminderInterval.FixedTime(hour = value, minute = minute)
    else -> ReminderInterval.None
}
