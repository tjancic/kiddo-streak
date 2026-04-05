package com.kiddostreak.feature.streak.domain.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

data class Streak(
    val id: String,
    val name: String,
    val emoji: String,
    val colorHex: String,
    val frequency: StreakFrequency,
    val customDays: Set<DayOfWeek> = emptySet(),
    val reminderInterval: ReminderInterval,
    val createdAt: LocalDate,
    val isArchived: Boolean = false,
    val isPrimary: Boolean = false,
)
