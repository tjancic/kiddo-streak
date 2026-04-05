package com.kiddostreak.feature.streak.presentation.editor

import com.kiddostreak.feature.streak.domain.model.ReminderInterval
import com.kiddostreak.feature.streak.domain.model.StreakFrequency
import kotlinx.datetime.DayOfWeek

sealed interface StreakEditorAction {
    data class OnNameChange(val name: String) : StreakEditorAction
    data class OnEmojiSelect(val emoji: String) : StreakEditorAction
    data class OnColorSelect(val colorHex: String) : StreakEditorAction
    data class OnFrequencySelect(val frequency: StreakFrequency) : StreakEditorAction
    data class OnCustomDayToggle(val day: DayOfWeek) : StreakEditorAction
    data class OnReminderSelect(val interval: ReminderInterval) : StreakEditorAction
    data object OnSaveClick : StreakEditorAction
    data object OnBackClick : StreakEditorAction
}
