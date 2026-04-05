package com.kiddostreak.feature.streak.domain.model

sealed interface ReminderInterval {
    data object None : ReminderInterval
    data class Periodic(val hours: Int) : ReminderInterval
    data class FixedTime(val hour: Int, val minute: Int) : ReminderInterval
}
