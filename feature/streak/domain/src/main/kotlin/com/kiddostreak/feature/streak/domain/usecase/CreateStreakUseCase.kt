package com.kiddostreak.feature.streak.domain.usecase

import com.kiddostreak.core.domain.Result
import com.kiddostreak.feature.streak.domain.error.StreakValidationError
import com.kiddostreak.feature.streak.domain.model.ReminderInterval
import com.kiddostreak.feature.streak.domain.model.Streak
import com.kiddostreak.feature.streak.domain.model.StreakFrequency
import com.kiddostreak.feature.streak.domain.repository.StreakRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.util.UUID

class CreateStreakUseCase(
    private val streakRepository: StreakRepository,
    private val syncReminderSchedule: SyncReminderScheduleUseCase,
) {

    suspend operator fun invoke(
        name: String,
        emoji: String,
        colorHex: String,
        frequency: StreakFrequency,
        customDays: Set<DayOfWeek> = emptySet(),
        reminderInterval: ReminderInterval = ReminderInterval.None
    ): Result<Streak, StreakValidationError> {
        val trimmedName = name.trim()

        if (trimmedName.isEmpty()) {
            return Result.Failure(StreakValidationError.EMPTY_NAME)
        }
        if (trimmedName.length > MAX_NAME_LENGTH) {
            return Result.Failure(StreakValidationError.NAME_TOO_LONG)
        }
        if (!COLOR_HEX_REGEX.matches(colorHex)) {
            return Result.Failure(StreakValidationError.INVALID_COLOR)
        }
        if (frequency == StreakFrequency.CUSTOM && customDays.isEmpty()) {
            return Result.Failure(StreakValidationError.CUSTOM_DAYS_EMPTY)
        }

        val streak = Streak(
            id = UUID.randomUUID().toString(),
            name = trimmedName,
            emoji = emoji,
            colorHex = colorHex,
            frequency = frequency,
            customDays = customDays,
            reminderInterval = reminderInterval,
            createdAt = Clock.System.todayIn(TimeZone.currentSystemDefault())
        )

        streakRepository.createStreak(streak)
        syncReminderSchedule()

        return Result.Success(streak)
    }

    private companion object {
        const val MAX_NAME_LENGTH = 50
        val COLOR_HEX_REGEX = Regex("^#[0-9A-Fa-f]{6}$")
    }
}
