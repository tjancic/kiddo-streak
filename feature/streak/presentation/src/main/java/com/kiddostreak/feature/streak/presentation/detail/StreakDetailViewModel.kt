package com.kiddostreak.feature.streak.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiddostreak.core.domain.onFailure
import com.kiddostreak.core.domain.onSuccess
import com.kiddostreak.core.presentation.toUiText
import com.kiddostreak.feature.streak.domain.usecase.ArchiveStreakUseCase
import com.kiddostreak.feature.streak.domain.usecase.CompleteStreakUseCase
import com.kiddostreak.feature.streak.domain.usecase.GetStreakStatsUseCase
import com.kiddostreak.feature.streak.domain.usecase.SyncReminderScheduleUseCase
import com.kiddostreak.feature.streak.domain.usecase.UncompleteStreakUseCase
import com.kiddostreak.feature.streak.domain.repository.StreakRepository
import com.kiddostreak.feature.streak.presentation.mapper.toUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.todayIn

class StreakDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val streakRepository: StreakRepository,
    private val getStreakStats: GetStreakStatsUseCase,
    private val completeStreak: CompleteStreakUseCase,
    private val uncompleteStreak: UncompleteStreakUseCase,
    private val archiveStreak: ArchiveStreakUseCase,
    private val syncReminderSchedule: SyncReminderScheduleUseCase,
) : ViewModel() {

    private val streakId: String = checkNotNull(savedStateHandle["streakId"])

    private val _state = MutableStateFlow(StreakDetailState())
    val state: StateFlow<StreakDetailState> = _state.asStateFlow()

    private val _events = Channel<StreakDetailEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadStreakData()
    }

    fun onAction(action: StreakDetailAction) {
        when (action) {
            StreakDetailAction.OnCheckInClick -> handleCheckIn()
            StreakDetailAction.OnEditClick -> {
                viewModelScope.launch {
                    _events.send(StreakDetailEvent.NavigateToEdit(streakId))
                }
            }

            StreakDetailAction.OnArchiveClick -> handleArchive()
            StreakDetailAction.OnSetAsPrimaryClick -> handleSetAsPrimary()
            StreakDetailAction.OnBackClick -> {
                viewModelScope.launch {
                    _events.send(StreakDetailEvent.NavigateBack)
                }
            }
        }
    }

    private fun loadStreakData() {
        viewModelScope.launch {
            combine(
                streakRepository.getStreakById(streakId),
                streakRepository.getCompletionsForStreak(streakId),
            ) { streak, completions ->
                Pair(streak, completions)
            }.collect { (streak, completions) ->
                if (streak == null) {
                    _events.send(StreakDetailEvent.NavigateBack)
                    return@collect
                }

                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                val stats = getStreakStats(streak, completions, today)
                val isCompletedToday = completions.any { it.date == today }
                val javaCompletedDates = completions
                    .map { it.date.toJavaLocalDate() }
                    .toSet()

                _state.update {
                    it.copy(
                        streakName = streak.name,
                        streakEmoji = streak.emoji,
                        streakColorHex = streak.colorHex,
                        stats = stats.toUi(),
                        completedDates = javaCompletedDates,
                        isCompletedToday = isCompletedToday,
                        isPrimary = streak.isPrimary,
                        isLoading = false,
                    )
                }
            }
        }
    }

    private fun handleCheckIn() {
        viewModelScope.launch {
            if (_state.value.isCompletedToday) {
                uncompleteStreak(streakId)
                    .onSuccess { _events.send(StreakDetailEvent.WidgetUpdateNeeded) }
                    .onFailure { error ->
                        _events.send(StreakDetailEvent.ShowSnackbar(error.toUiText()))
                    }
            } else {
                completeStreak(streakId)
                    .onSuccess { _events.send(StreakDetailEvent.WidgetUpdateNeeded) }
                    .onFailure { error ->
                        _events.send(StreakDetailEvent.ShowSnackbar(error.toUiText()))
                    }
            }
        }
    }

    private fun handleSetAsPrimary() {
        viewModelScope.launch {
            streakRepository.setPrimaryStreak(streakId)
                .onSuccess { _events.send(StreakDetailEvent.WidgetUpdateNeeded) }
        }
    }

    private fun handleArchive() {
        viewModelScope.launch {
            archiveStreak(streakId)
                .onSuccess {
                    syncReminderSchedule()
                    _events.send(StreakDetailEvent.WidgetUpdateNeeded)
                    _events.send(StreakDetailEvent.NavigateBack)
                }
                .onFailure { error ->
                    _events.send(StreakDetailEvent.ShowSnackbar(error.toUiText()))
                }
        }
    }
}
