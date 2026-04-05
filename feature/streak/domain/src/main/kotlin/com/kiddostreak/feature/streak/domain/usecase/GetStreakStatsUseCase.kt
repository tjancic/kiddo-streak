package com.kiddostreak.feature.streak.domain.usecase

import com.kiddostreak.feature.streak.domain.model.Milestone
import com.kiddostreak.feature.streak.domain.model.Streak
import com.kiddostreak.feature.streak.domain.model.StreakCompletion
import com.kiddostreak.feature.streak.domain.model.StreakStats
import com.kiddostreak.feature.streak.domain.model.isEligibleDay
import com.kiddostreak.feature.streak.domain.model.nextEligibleDay
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlinx.datetime.Clock

class GetStreakStatsUseCase {

    operator fun invoke(
        streak: Streak,
        completions: List<StreakCompletion>,
        today: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
    ): StreakStats {
        val completedDates = completions
            .filter { it.streakId == streak.id }
            .map { it.date }
            .toSortedSet()

        val currentStreak = calculateCurrentStreak(streak, completedDates, today)
        val longestStreak = calculateLongestStreak(streak, completedDates)
        val totalCompletions = completedDates.size
        val completionRate = calculateCompletionRate(streak, completedDates, today)
        val currentMilestone = Milestone.forDay(currentStreak)
            ?: Milestone.entries
                .filter { it.days <= currentStreak }
                .maxByOrNull { it.days }
        val nextMilestone = Milestone.nextAfter(currentStreak)

        return StreakStats(
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            totalCompletions = totalCompletions,
            completionRate = completionRate,
            currentMilestone = currentMilestone,
            nextMilestone = nextMilestone
        )
    }

    private fun calculateCurrentStreak(
        streak: Streak,
        completedDates: Set<LocalDate>,
        today: LocalDate
    ): Int {
        var count = 0
        var date = if (completedDates.contains(today)) today else today.minus(1, DateTimeUnit.DAY)

        while (true) {
            if (!isEligibleDay(streak.frequency, date, streak.customDays)) {
                date = date.minus(1, DateTimeUnit.DAY)
                continue
            }
            if (completedDates.contains(date)) {
                count++
                date = date.minus(1, DateTimeUnit.DAY)
            } else {
                break
            }
        }
        return count
    }

    private fun calculateLongestStreak(
        streak: Streak,
        completedDates: Set<LocalDate>
    ): Int {
        if (completedDates.isEmpty()) return 0

        val sortedDates = completedDates.sorted()
        var longest = 0
        var current = 0
        var expectedDate: LocalDate? = null

        for (date in sortedDates) {
            if (expectedDate == null) {
                current = 1
                expectedDate = nextEligibleDay(streak.frequency, date.plus(1, DateTimeUnit.DAY), streak.customDays)
            } else if (date == expectedDate) {
                current++
                expectedDate = nextEligibleDay(streak.frequency, date.plus(1, DateTimeUnit.DAY), streak.customDays)
            } else {
                longest = maxOf(longest, current)
                current = 1
                expectedDate = nextEligibleDay(streak.frequency, date.plus(1, DateTimeUnit.DAY), streak.customDays)
            }
        }
        return maxOf(longest, current)
    }

    private fun calculateCompletionRate(
        streak: Streak,
        completedDates: Set<LocalDate>,
        today: LocalDate
    ): Float {
        var eligibleDays = 0
        var date = streak.createdAt
        while (date <= today) {
            if (isEligibleDay(streak.frequency, date, streak.customDays)) {
                eligibleDays++
            }
            date = date.plus(1, DateTimeUnit.DAY)
        }
        if (eligibleDays == 0) return 0f
        return completedDates.size.toFloat() / eligibleDays.toFloat()
    }
}
