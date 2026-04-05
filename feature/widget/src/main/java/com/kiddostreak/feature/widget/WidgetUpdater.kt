package com.kiddostreak.feature.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent

object WidgetUpdater {

    fun updateAll(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)

        listOf(
            StreakWidgetTinyReceiver::class.java,
            StreakWidgetSmallReceiver::class.java,
            StreakWidgetMediumReceiver::class.java,
        ).forEach { receiverClass ->
            val componentName = ComponentName(context, receiverClass)
            val ids = appWidgetManager.getAppWidgetIds(componentName)
            if (ids.isNotEmpty()) {
                val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
                    component = componentName
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                }
                context.sendBroadcast(intent)
            }
        }
    }
}
