package com.kiddostreak.core.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kiddostreak.feature.streak.domain.usecase.SyncReminderScheduleUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.getKoin

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val syncReminderSchedule: SyncReminderScheduleUseCase = getKoin().get()
                syncReminderSchedule()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
