package com.kiddostreak.feature.streak.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiddostreak.core.domain.onFailure
import com.kiddostreak.core.domain.onSuccess
import com.kiddostreak.core.presentation.toUiText
import com.kiddostreak.feature.streak.domain.model.Milestone
import com.kiddostreak.feature.streak.domain.usecase.CompleteStreakUseCase
import com.kiddostreak.feature.streak.domain.usecase.GetActiveStreaksWithStatsUseCase
import com.kiddostreak.feature.streak.domain.usecase.UncompleteStreakUseCase
import com.kiddostreak.feature.streak.presentation.mapper.toUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.todayIn
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeViewModel(
    private val getActiveStreaksWithStats: GetActiveStreaksWithStatsUseCase,
    private val completeStreak: CompleteStreakUseCase,
    private val uncompleteStreak: UncompleteStreakUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _events = Channel<HomeEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadStreaks()
        formatToday()
    }

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.OnStreakTap -> {
                viewModelScope.launch {
                    _events.send(HomeEvent.NavigateToStreakDetail(action.streakId))
                }
            }

            is HomeAction.OnStreakCheckIn -> handleCheckIn(action.streakId)
            is HomeAction.OnStreakUncheck -> handleUncheck(action.streakId)

            HomeAction.OnAddStreakClick -> {
                viewModelScope.launch {
                    _events.send(HomeEvent.NavigateToCreateStreak)
                }
            }

            HomeAction.OnSettingsClick -> {
                viewModelScope.launch {
                    _events.send(HomeEvent.NavigateToSettings)
                }
            }
        }
    }

    private fun loadStreaks() {
        viewModelScope.launch {
            getActiveStreaksWithStats().collect { streaksList ->
                _state.update { state ->
                    state.copy(
                        streaks = streaksList.map { it.toUi() },
                        isLoading = false,
                    )
                }
            }
        }
    }

    private fun formatToday() {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault())
        val formatted = today.toJavaLocalDate().format(formatter)
        _state.update { it.copy(todayFormatted = formatted) }
    }

    private fun handleCheckIn(streakId: String) {
        viewModelScope.launch {
            val streakBeforeCheckIn = _state.value.streaks.find { it.id == streakId }
            val currentStreakDays = streakBeforeCheckIn?.currentStreak ?: 0

            completeStreak(streakId)
                .onSuccess {
                    _events.send(HomeEvent.WidgetUpdateNeeded)
                    val newDay = currentStreakDays + 1
                    val milestone = Milestone.forDay(newDay)
                    if (milestone != null && streakBeforeCheckIn != null) {
                        _events.send(
                            HomeEvent.ShowMilestoneCelebration(
                                streakName = streakBeforeCheckIn.name,
                                milestone = milestone.days,
                            ),
                        )
                    }
                }
                .onFailure { error ->
                    _events.send(HomeEvent.ShowSnackbar(error.toUiText()))
                }
        }
    }

    private fun handleUncheck(streakId: String) {
        viewModelScope.launch {
            uncompleteStreak(streakId)
                .onSuccess {
                    _events.send(HomeEvent.WidgetUpdateNeeded)
                }
                .onFailure { error ->
                    _events.send(HomeEvent.ShowSnackbar(error.toUiText()))
                }
        }
    }
}
