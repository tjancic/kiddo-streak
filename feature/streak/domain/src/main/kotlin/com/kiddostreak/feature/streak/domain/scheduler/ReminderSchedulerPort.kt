package com.kiddostreak.feature.streak.domain.scheduler

import com.kiddostreak.feature.streak.domain.model.ReminderInterval

interface ReminderSchedulerPort {
    fun schedule(reminderInterval: ReminderInterval)
    fun cancel()
}
