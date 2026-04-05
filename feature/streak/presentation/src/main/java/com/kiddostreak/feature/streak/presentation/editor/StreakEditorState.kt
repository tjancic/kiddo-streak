package com.kiddostreak.feature.streak.presentation.editor

import com.kiddostreak.core.presentation.UiText
import com.kiddostreak.feature.streak.domain.model.ReminderInterval
import com.kiddostreak.feature.streak.domain.model.StreakFrequency
import kotlinx.datetime.DayOfWeek

data class StreakEditorState(
    val name: String = "",
    val selectedEmoji: String = "\uD83D\uDD25",
    val selectedColorHex: String = "#FF6B35",
    val frequency: StreakFrequency = StreakFrequency.DAILY,
    val customDays: Set<DayOfWeek> = emptySet(),
    val reminderInterval: ReminderInterval = ReminderInterval.None,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val nameError: UiText? = null,
)
