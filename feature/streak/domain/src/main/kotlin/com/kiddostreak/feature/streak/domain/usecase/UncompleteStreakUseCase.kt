package com.kiddostreak.feature.streak.domain.usecase

import com.kiddostreak.core.domain.DataError
import com.kiddostreak.core.domain.EmptyResult
import com.kiddostreak.feature.streak.domain.repository.StreakRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class UncompleteStreakUseCase(
    private val streakRepository: StreakRepository
) {

    suspend operator fun invoke(
        streakId: String,
        date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
    ): EmptyResult<DataError.Local> {
        return streakRepository.uncompleteStreak(streakId, date)
    }
}
