package com.kiddostreak.feature.streak.domain.usecase

import com.kiddostreak.feature.streak.domain.repository.StreakRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

class GetStreakCalendarUseCase(
    private val streakRepository: StreakRepository
) {

    operator fun invoke(streakId: String): Flow<Map<Pair<Int, Month>, Set<LocalDate>>> {
        return streakRepository.getCompletionsForStreak(streakId).map { completions ->
            completions
                .map { it.date }
                .groupBy { Pair(it.year, it.month) }
                .mapValues { (_, dates) -> dates.toSet() }
        }
    }
}
