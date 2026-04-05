package com.kiddostreak.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationChannelManager {

    const val REMINDER_CHANNEL_ID = "streak_reminders"
    const val MILESTONE_CHANNEL_ID = "streak_milestones"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val reminderChannel = NotificationChannel(
            REMINDER_CHANNEL_ID,
            "Streak Reminders",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Daily reminders to complete your active streaks"
        }

        val milestoneChannel = NotificationChannel(
            MILESTONE_CHANNEL_ID,
            "Milestone Celebrations",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "Celebrate when you hit streak milestones"
        }

        notificationManager.createNotificationChannels(
            listOf(reminderChannel, milestoneChannel),
        )
    }
}
