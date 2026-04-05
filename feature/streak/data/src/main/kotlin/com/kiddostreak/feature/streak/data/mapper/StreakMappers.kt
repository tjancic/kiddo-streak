package com.kiddostreak.feature.streak.data.mapper

import com.kiddostreak.feature.streak.data.dto.StreakCompletionDto
import com.kiddostreak.feature.streak.data.dto.StreakDto
import com.kiddostreak.feature.streak.domain.model.ReminderIntervalDto
import com.kiddostreak.feature.streak.domain.model.Streak
import com.kiddostreak.feature.streak.domain.model.StreakCompletion
import com.kiddostreak.feature.streak.domain.model.StreakFrequency
import com.kiddostreak.feature.streak.domain.model.toDto
import com.kiddostreak.feature.streak.domain.model.toReminderInterval
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

fun StreakDto.toStreak(): Streak {
    val parsedCustomDays = customDays.mapNotNull { name ->
        runCatching { DayOfWeek.valueOf(name) }.getOrNull()
    }.toSet()

    // Backward compat: CUSTOM with no days configured falls back to DAILY
    val resolvedFrequency = when (frequency.uppercase()) {
        "DAILY" -> StreakFrequency.DAILY
        "WEEKDAYS" -> StreakFrequency.WEEKDAYS
        "WEEKENDS" -> StreakFrequency.WEEKENDS
        "CUSTOM" -> if (parsedCustomDays.isEmpty()) StreakFrequency.DAILY else StreakFrequency.CUSTOM
        else -> StreakFrequency.DAILY
    }

    return Streak(
        id = id,
        name = name,
        emoji = emoji,
        colorHex = colorHex,
        frequency = resolvedFrequency,
        customDays = parsedCustomDays,
        reminderInterval = ReminderIntervalDto(
            type = reminderType,
            value = reminderValue,
            minute = reminderMinute,
        ).toReminderInterval(),
        createdAt = LocalDate.parse(createdAt),
        isArchived = isArchived,
        isPrimary = isPrimary,
    )
}

fun Streak.toDto(): StreakDto {
    val reminderDto = reminderInterval.toDto()
    return StreakDto(
        id = id,
        name = name,
        emoji = emoji,
        colorHex = colorHex,
        frequency = frequency.name,
        reminderType = reminderDto.type,
        reminderValue = reminderDto.value,
        reminderMinute = reminderDto.minute,
        createdAt = createdAt.toString(),
        isArchived = isArchived,
        isPrimary = isPrimary,
        customDays = customDays.map { it.name },
    )
}

fun StreakCompletionDto.toCompletion(): StreakCompletion = StreakCompletion(
    streakId = streakId,
    date = LocalDate.parse(date),
    completedAt = Instant.fromEpochMilliseconds(completedAt)
)

fun StreakCompletion.toDto(): StreakCompletionDto = StreakCompletionDto(
    streakId = streakId,
    date = date.toString(),
    completedAt = completedAt.toEpochMilliseconds()
)
