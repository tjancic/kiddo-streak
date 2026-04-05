package com.kiddostreak.feature.streak.presentation.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiddostreak.core.domain.onFailure
import com.kiddostreak.core.domain.onSuccess
import com.kiddostreak.core.presentation.UiText
import com.kiddostreak.feature.streak.domain.error.StreakValidationError
import com.kiddostreak.feature.streak.domain.model.Streak
import com.kiddostreak.feature.streak.domain.model.StreakFrequency
import com.kiddostreak.feature.streak.domain.repository.StreakRepository
import com.kiddostreak.feature.streak.domain.usecase.CreateStreakUseCase
import com.kiddostreak.feature.streak.domain.usecase.SyncReminderScheduleUseCase
import com.kiddostreak.feature.streak.presentation.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StreakEditorViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val createStreak: CreateStreakUseCase,
    private val streakRepository: StreakRepository,
    private val syncReminderSchedule: SyncReminderScheduleUseCase,
) : ViewModel() {

    private val streakId: String? = savedStateHandle["streakId"]

    private val _state = MutableStateFlow(StreakEditorState())
    val state: StateFlow<StreakEditorState> = _state.asStateFlow()

    private val _events = Channel<StreakEditorEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var existingStreak: Streak? = null

    init {
        // Restore name from SavedStateHandle if process-death occurred
        savedStateHandle.get<String>("editor_name")?.let { savedName ->
            _state.update { it.copy(name = savedName) }
        }

        if (streakId != null) {
            loadExistingStreak(streakId)
        }
    }

    fun onAction(action: StreakEditorAction) {
        when (action) {
            is StreakEditorAction.OnNameChange -> {
                _state.update { it.copy(name = action.name, nameError = null) }
                savedStateHandle["editor_name"] = action.name
            }

            is StreakEditorAction.OnEmojiSelect -> {
                _state.update { it.copy(selectedEmoji = action.emoji) }
            }

            is StreakEditorAction.OnColorSelect -> {
                _state.update { it.copy(selectedColorHex = action.colorHex) }
            }

            is StreakEditorAction.OnFrequencySelect -> {
                _state.update {
                    it.copy(
                        frequency = action.frequency,
                        customDays = if (action.frequency == StreakFrequency.CUSTOM) it.customDays else emptySet(),
                    )
                }
            }

            is StreakEditorAction.OnCustomDayToggle -> {
                _state.update {
                    val updated = if (action.day in it.customDays) {
                        it.customDays - action.day
                    } else {
                        it.customDays + action.day
                    }
                    it.copy(customDays = updated)
                }
            }

            is StreakEditorAction.OnReminderSelect -> {
                _state.update { it.copy(reminderInterval = action.interval) }
            }

            StreakEditorAction.OnSaveClick -> save()
            StreakEditorAction.OnBackClick -> {
                viewModelScope.launch {
                    _events.send(StreakEditorEvent.NavigateBack)
                }
            }
        }
    }

    private fun loadExistingStreak(id: String) {
        viewModelScope.launch {
            val streak = streakRepository.getStreakById(id).first() ?: return@launch
            existingStreak = streak
            _state.update {
                it.copy(
                    name = it.name.ifEmpty { streak.name },
                    selectedEmoji = streak.emoji,
                    selectedColorHex = streak.colorHex,
                    frequency = streak.frequency,
                    customDays = streak.customDays,
                    reminderInterval = streak.reminderInterval,
                    isEditing = true,
                )
            }
        }
    }

    private fun save() {
        val currentState = _state.value
        if (currentState.isSaving) return

        _state.update { it.copy(isSaving = true, nameError = null) }

        viewModelScope.launch {
            if (currentState.isEditing && existingStreak != null) {
                updateExistingStreak(currentState)
            } else {
                createNewStreak(currentState)
            }
        }
    }

    private suspend fun createNewStreak(currentState: StreakEditorState) {
        createStreak(
            name = currentState.name,
            emoji = currentState.selectedEmoji,
            colorHex = currentState.selectedColorHex,
            frequency = currentState.frequency,
            customDays = currentState.customDays,
            reminderInterval = currentState.reminderInterval,
        )
            .onSuccess {
                _state.update { it.copy(isSaving = false) }
                _events.send(StreakEditorEvent.NavigateBack)
            }
            .onFailure { error ->
                _state.update {
                    it.copy(
                        isSaving = false,
                        nameError = error.toUiText(),
                    )
                }
            }
    }

    private suspend fun updateExistingStreak(currentState: StreakEditorState) {
        val updated = existingStreak!!.copy(
            name = currentState.name.trim(),
            emoji = currentState.selectedEmoji,
            colorHex = currentState.selectedColorHex,
            frequency = currentState.frequency,
            customDays = currentState.customDays,
            reminderInterval = currentState.reminderInterval,
        )

        streakRepository.updateStreak(updated)
            .onSuccess {
                syncReminderSchedule()
                _state.update { it.copy(isSaving = false) }
                _events.send(StreakEditorEvent.NavigateBack)
            }
            .onFailure {
                _state.update { it.copy(isSaving = false) }
                _events.send(
                    StreakEditorEvent.ShowSnackbar(
                        UiText.StringResource(R.string.editor_save_error),
                    ),
                )
            }
    }

    private fun StreakValidationError.toUiText(): UiText {
        return when (this) {
            StreakValidationError.EMPTY_NAME -> UiText.StringResource(R.string.editor_error_empty_name)
            StreakValidationError.NAME_TOO_LONG -> UiText.StringResource(R.string.editor_error_name_too_long)
            StreakValidationError.INVALID_COLOR -> UiText.StringResource(R.string.editor_error_invalid_color)
            StreakValidationError.CUSTOM_DAYS_EMPTY -> UiText.StringResource(R.string.editor_error_custom_days_empty)
        }
    }
}
