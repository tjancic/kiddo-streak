package com.kiddostreak.feature.streak.domain.usecase

import com.kiddostreak.feature.streak.domain.model.StreakWithStats
import com.kiddostreak.feature.streak.domain.repository.StreakRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class GetActiveStreaksWithStatsUseCase(
    private val streakRepository: StreakRepository,
    private val getStreakStats: GetStreakStatsUseCase
) {

    operator fun invoke(): Flow<List<StreakWithStats>> {
        return combine(
            streakRepository.getActiveStreaks(),
            streakRepository.getAllCompletions()
        ) { streaks, allCompletions ->
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val completionsByStreak = allCompletions.groupBy { it.streakId }

            streaks.map { streak ->
                val streakCompletions = completionsByStreak[streak.id].orEmpty()
                val stats = getStreakStats(streak, streakCompletions, today)
                val isCompletedToday = streakCompletions.any { it.date == today }

                StreakWithStats(
                    streak = streak,
                    stats = stats,
                    isCompletedToday = isCompletedToday
                )
            }
        }
    }
}
