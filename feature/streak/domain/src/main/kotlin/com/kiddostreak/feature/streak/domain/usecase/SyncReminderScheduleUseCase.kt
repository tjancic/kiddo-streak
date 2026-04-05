package com.kiddostreak.feature.streak.domain.usecase

import com.kiddostreak.feature.streak.domain.model.ReminderInterval
import com.kiddostreak.feature.streak.domain.repository.StreakRepository
import com.kiddostreak.feature.streak.domain.scheduler.ReminderSchedulerPort
import kotlinx.coroutines.flow.first

class SyncReminderScheduleUseCase(
    private val streakRepository: StreakRepository,
    private val reminderScheduler: ReminderSchedulerPort,
) {

    suspend operator fun invoke() {
        val activeStreaks = streakRepository.getActiveStreaks().first()
        val activeReminders = activeStreaks
            .map { it.reminderInterval }
            .filter { it !is ReminderInterval.None }

        if (activeReminders.isEmpty()) {
            reminderScheduler.cancel()
            return
        }

        val bestReminder = activeReminders
            .sortedBy { if (it is ReminderInterval.FixedTime) 0 else 1 }
            .first()

        reminderScheduler.schedule(bestReminder)
    }
}
