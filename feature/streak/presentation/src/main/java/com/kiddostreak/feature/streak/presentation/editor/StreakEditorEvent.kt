package com.kiddostreak.feature.streak.presentation.editor

import com.kiddostreak.core.presentation.UiText

sealed interface StreakEditorEvent {
    data object NavigateBack : StreakEditorEvent
    data class ShowSnackbar(val message: UiText) : StreakEditorEvent
}
