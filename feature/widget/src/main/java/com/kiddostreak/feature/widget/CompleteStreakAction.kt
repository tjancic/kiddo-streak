package com.kiddostreak.feature.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.kiddostreak.feature.streak.domain.repository.StreakRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.java.KoinJavaComponent.getKoin

class CompleteStreakAction : ActionCallback {

    companion object {
        val STREAK_ID_KEY = ActionParameters.Key<String>("streak_id")
        val VARIANT_KEY = ActionParameters.Key<String>("variant")
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val streakId = parameters[STREAK_ID_KEY] ?: return
        val variantName = parameters[VARIANT_KEY] ?: WidgetVariant.MEDIUM.name
        val variant = WidgetVariant.valueOf(variantName)

        val repository: StreakRepository = getKoin().get()
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

        repository.completeStreak(streakId, today)

        StreakWidget(variant).update(context, glanceId)
    }
}
