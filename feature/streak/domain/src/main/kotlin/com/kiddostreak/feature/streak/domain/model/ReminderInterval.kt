package com.kiddostreak.feature.streak.domain.model

sealed interface ReminderInterval {
    data object None : ReminderInterval
    data class Periodic(
        val hours: Int,
        val fromHour: Int = DEFAULT_FROM_HOUR,
        val toHour: Int = DEFAULT_TO_HOUR,
    ) : ReminderInterval
    data class FixedTime(val hour: Int, val minute: Int) : ReminderInterval
}

private const val DEFAULT_FROM_HOUR = 8
private const val DEFAULT_TO_HOUR = 22
