package com.kiddostreak.core.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.kiddostreak.feature.streak.domain.model.ReminderInterval
import com.kiddostreak.feature.streak.domain.scheduler.ReminderSchedulerPort
import java.util.Calendar
import java.util.concurrent.TimeUnit

class ReminderScheduler(private val context: Context) : ReminderSchedulerPort {

    private val workManager = WorkManager.getInstance(context)

    override fun schedule(reminderInterval: ReminderInterval) {
        when (reminderInterval) {
            is ReminderInterval.None -> cancelAllReminders()
            is ReminderInterval.Periodic -> schedulePeriodicReminder(
                reminderInterval.hours,
                reminderInterval.fromHour,
                reminderInterval.toHour,
            )
            is ReminderInterval.FixedTime -> scheduleFixedTimeReminder(
                reminderInterval.hour,
                reminderInterval.minute,
            )
        }
    }

    override fun cancel() {
        cancelAllReminders()
    }

    private fun schedulePeriodicReminder(intervalHours: Int, fromHour: Int, toHour: Int) {
        val inputData = workDataOf(
            ReminderWorker.KEY_ACTIVE_FROM_HOUR to fromHour,
            ReminderWorker.KEY_ACTIVE_TO_HOUR to toHour,
        )

        val request = PeriodicWorkRequestBuilder<ReminderWorker>(
            intervalHours.toLong(),
            TimeUnit.HOURS,
        )
            .setInputData(inputData)
            .build()

        workManager.enqueueUniquePeriodicWork(
            ReminderWorker.WORK_NAME_PERIODIC,
            ExistingPeriodicWorkPolicy.UPDATE,
            request,
        )
    }

    private fun scheduleFixedTimeReminder(hour: Int, minute: Int) {
        val delayMillis = calculateDelayUntil(hour, minute)

        val inputData = workDataOf(
            ReminderWorker.KEY_FIXED_HOUR to hour,
            ReminderWorker.KEY_FIXED_MINUTE to minute,
        )

        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        workManager.enqueueUniqueWork(
            ReminderWorker.WORK_NAME_FIXED,
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }

    private fun cancelAllReminders() {
        workManager.cancelUniqueWork(ReminderWorker.WORK_NAME_PERIODIC)
        workManager.cancelUniqueWork(ReminderWorker.WORK_NAME_FIXED)
    }

    private fun calculateDelayUntil(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (target.before(now) || target == now) {
            target.add(Calendar.DAY_OF_MONTH, 1)
        }

        return target.timeInMillis - now.timeInMillis
    }
}
