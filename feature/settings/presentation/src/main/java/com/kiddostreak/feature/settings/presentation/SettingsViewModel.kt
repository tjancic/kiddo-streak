package com.kiddostreak.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiddostreak.feature.settings.domain.model.AppSettings
import com.kiddostreak.feature.settings.domain.repository.SettingsRepository
import com.kiddostreak.feature.streak.domain.model.ReminderInterval
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state
        .onStart { loadSettings() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsState(),
        )

    private val _events = Channel<SettingsEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.OnToggleReminders -> updateReminders(action.enabled)
            is SettingsAction.OnReminderIntervalChange -> updateInterval(action.interval)
            is SettingsAction.OnToggleDynamicColors -> updateDynamicColors(action.enabled)
            is SettingsAction.OnToggleShowCompleted -> updateShowCompleted(action.show)
            is SettingsAction.OnBackClick -> {
                viewModelScope.launch { _events.send(SettingsEvent.NavigateBack) }
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val settings = settingsRepository.getSettings().first()
            _state.update {
                it.copy(
                    globalRemindersEnabled = settings.globalRemindersEnabled,
                    defaultReminderInterval = settings.defaultReminderInterval,
                    useDynamicColors = settings.useDynamicColors,
                    showCompletedStreaks = settings.showCompletedStreaks,
                    isLoading = false,
                )
            }
        }
    }

    private fun updateReminders(enabled: Boolean) {
        _state.update { it.copy(globalRemindersEnabled = enabled) }
        persistCurrentSettings()
    }

    private fun updateInterval(interval: ReminderInterval) {
        _state.update { it.copy(defaultReminderInterval = interval) }
        persistCurrentSettings()
    }

    private fun updateDynamicColors(enabled: Boolean) {
        _state.update { it.copy(useDynamicColors = enabled) }
        persistCurrentSettings()
    }

    private fun updateShowCompleted(show: Boolean) {
        _state.update { it.copy(showCompletedStreaks = show) }
        persistCurrentSettings()
    }

    private fun persistCurrentSettings() {
        viewModelScope.launch {
            val current = _state.value
            settingsRepository.updateSettings(
                AppSettings(
                    globalRemindersEnabled = current.globalRemindersEnabled,
                    defaultReminderInterval = current.defaultReminderInterval,
                    useDynamicColors = current.useDynamicColors,
                    showCompletedStreaks = current.showCompletedStreaks,
                )
            )
        }
    }
}
