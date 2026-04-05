package com.kiddostreak.feature.streak.domain.model

data class ReminderIntervalDto(
    val type: String,
    val value: Int,
    val minute: Int,
)

fun ReminderInterval.toDto(): ReminderIntervalDto = when (this) {
    is ReminderInterval.None -> ReminderIntervalDto("none", 0, 0)
    is ReminderInterval.Periodic -> ReminderIntervalDto("periodic", hours, 0)
    is ReminderInterval.FixedTime -> ReminderIntervalDto("fixed", hour, minute)
}

fun ReminderIntervalDto.toReminderInterval(): ReminderInterval = when (type) {
    "periodic" -> ReminderInterval.Periodic(hours = value)
    "fixed" -> ReminderInterval.FixedTime(hour = value, minute = minute)
    else -> ReminderInterval.None
}
