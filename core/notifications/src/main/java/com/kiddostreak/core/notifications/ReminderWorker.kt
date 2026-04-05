package com.kiddostreak.core.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kiddostreak.feature.streak.domain.model.isEligibleDay
import com.kiddostreak.feature.streak.domain.repository.StreakRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.java.KoinJavaComponent.getKoin

class ReminderWorker(
    private val appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME_PERIODIC = "streak_reminder_periodic"
        const val WORK_NAME_FIXED = "streak_reminder_fixed"
        const val KEY_FIXED_HOUR = "fixed_hour"
        const val KEY_FIXED_MINUTE = "fixed_minute"
        const val KEY_ACTIVE_FROM_HOUR = "active_from_hour"
        const val KEY_ACTIVE_TO_HOUR = "active_to_hour"
        private const val NOTIFICATION_ID = 1001
    }

    override suspend fun doWork(): Result {
        // Skip notification if outside the active window for periodic reminders
        val fromHour = inputData.getInt(KEY_ACTIVE_FROM_HOUR, -1)
        val toHour = inputData.getInt(KEY_ACTIVE_TO_HOUR, -1)
        if (fromHour >= 0 && toHour >= 0) {
            val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
            if (currentHour < fromHour || currentHour >= toHour) {
                return Result.success()
            }
        }

        val repository: StreakRepository = getKoin().get()
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

        val (activeStreaks, todayCompletions) = kotlinx.coroutines.flow.combine(
            repository.getActiveStreaks(),
            repository.getCompletionsForDate(today),
        ) { streaks, comps -> streaks to comps }.first()
        val completedIds = todayCompletions.map { it.streakId }.toSet()

        val incompleteStreaks = activeStreaks
            .filter { it.id !in completedIds }
            .filter { isEligibleDay(it.frequency, today, it.customDays) }

        if (incompleteStreaks.isEmpty()) {
            rescheduleFixedTimeIfNeeded()
            return Result.success()
        }

        val contentText = when (incompleteStreaks.size) {
            1 -> "Keep it going! Complete \"${incompleteStreaks.first().name}\" to maintain your streak"
            else -> "You have ${incompleteStreaks.size} streaks waiting \u2014 don\u2019t break your chains!"
        }

        showNotification(contentText)
        rescheduleFixedTimeIfNeeded()

        return Result.success()
    }

    private fun rescheduleFixedTimeIfNeeded() {
        val hour = inputData.getInt(KEY_FIXED_HOUR, -1)
        val minute = inputData.getInt(KEY_FIXED_MINUTE, -1)
        if (hour >= 0 && minute >= 0) {
            val scheduler: ReminderScheduler = getKoin().get()
            scheduler.schedule(
                com.kiddostreak.feature.streak.domain.model.ReminderInterval.FixedTime(hour, minute),
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(contentText: String) {
        if (ActivityCompat.checkSelfPermission(
                appContext,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val launchIntent = Intent().apply {
            setClassName(appContext, "com.kiddostreak.app.MainActivity")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            appContext,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(
            appContext,
            NotificationChannelManager.REMINDER_CHANNEL_ID,
        )
            .setSmallIcon(R.drawable.ic_notification_flame)
            .setContentTitle("Streak Reminder")
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(appContext).notify(NOTIFICATION_ID, notification)
    }
}
