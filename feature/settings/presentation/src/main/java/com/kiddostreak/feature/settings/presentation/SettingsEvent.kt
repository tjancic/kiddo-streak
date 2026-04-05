package com.kiddostreak.feature.settings.presentation

sealed interface SettingsEvent {
    data object NavigateBack : SettingsEvent
}
