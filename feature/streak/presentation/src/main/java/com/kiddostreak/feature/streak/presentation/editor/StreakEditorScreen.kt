package com.kiddostreak.feature.streak.presentation.editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kiddostreak.core.designsystem.components.ColorPicker
import com.kiddostreak.core.designsystem.components.EmojiPicker
import com.kiddostreak.core.designsystem.components.KiddoStreakTopBar
import com.kiddostreak.core.presentation.ObserveAsEvents
import kotlinx.coroutines.launch
import com.kiddostreak.feature.streak.domain.model.ReminderInterval
import com.kiddostreak.feature.streak.domain.model.StreakFrequency
import com.kiddostreak.feature.streak.presentation.R
import kotlinx.datetime.DayOfWeek
import org.koin.androidx.compose.koinViewModel

@Composable
fun StreakEditorRoot(
    onNavigateBack: () -> Unit,
    viewModel: StreakEditorViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            StreakEditorEvent.NavigateBack -> onNavigateBack()
            is StreakEditorEvent.ShowSnackbar -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = event.message.asString(context),
                    )
                }
            }
        }
    }

    StreakEditorScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StreakEditorScreen(
    state: StreakEditorState,
    snackbarHostState: SnackbarHostState,
    onAction: (StreakEditorAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            KiddoStreakTopBar(
                title = if (state.isEditing) {
                    stringResource(R.string.editor_title_edit)
                } else {
                    stringResource(R.string.editor_title_new)
                },
                onBackClick = { onAction(StreakEditorAction.OnBackClick) },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
                .imePadding(),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Streak name
            OutlinedTextField(
                value = state.name,
                onValueChange = { onAction(StreakEditorAction.OnNameChange(it)) },
                label = { Text(stringResource(R.string.editor_name_label)) },
                placeholder = { Text(stringResource(R.string.editor_name_placeholder)) },
                isError = state.nameError != null,
                supportingText = state.nameError?.let { error ->
                    { Text(text = error.asString()) }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Emoji picker
            SectionHeader(text = stringResource(R.string.editor_emoji_section))
            Spacer(modifier = Modifier.height(8.dp))
            EmojiPicker(
                selectedEmoji = state.selectedEmoji,
                onEmojiSelected = { onAction(StreakEditorAction.OnEmojiSelect(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Color picker
            SectionHeader(text = stringResource(R.string.editor_color_section))
            Spacer(modifier = Modifier.height(8.dp))
            ColorPicker(
                selectedColorHex = state.selectedColorHex,
                onColorSelected = { onAction(StreakEditorAction.OnColorSelect(it)) },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Frequency
            SectionHeader(text = stringResource(R.string.editor_frequency_section))
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                StreakFrequency.entries.forEach { frequency ->
                    FilterChip(
                        selected = state.frequency == frequency,
                        onClick = { onAction(StreakEditorAction.OnFrequencySelect(frequency)) },
                        label = { Text(frequency.toDisplayString()) },
                    )
                }
            }

            // Custom day-of-week picker
            AnimatedVisibility(visible = state.frequency == StreakFrequency.CUSTOM) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.editor_custom_days_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        DayOfWeek.entries.forEach { day ->
                            FilterChip(
                                selected = day in state.customDays,
                                onClick = { onAction(StreakEditorAction.OnCustomDayToggle(day)) },
                                label = { Text(day.toAbbreviated()) },
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Reminder
            SectionHeader(text = stringResource(R.string.editor_reminder_section))
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                val currentPeriodic = state.reminderInterval as? ReminderInterval.Periodic
                val periodicHourOptions = listOf(
                    2 to stringResource(R.string.editor_reminder_2h),
                    4 to stringResource(R.string.editor_reminder_4h),
                    12 to stringResource(R.string.editor_reminder_12h),
                )

                FilterChip(
                    selected = state.reminderInterval is ReminderInterval.None,
                    onClick = { onAction(StreakEditorAction.OnReminderSelect(ReminderInterval.None)) },
                    label = { Text(stringResource(R.string.editor_reminder_none)) },
                )

                periodicHourOptions.forEach { (hours, label) ->
                    FilterChip(
                        selected = currentPeriodic?.hours == hours,
                        onClick = {
                            val interval = ReminderInterval.Periodic(
                                hours = hours,
                                fromHour = currentPeriodic?.fromHour ?: 8,
                                toHour = currentPeriodic?.toHour ?: 22,
                            )
                            onAction(StreakEditorAction.OnReminderSelect(interval))
                        },
                        label = { Text(label) },
                    )
                }

                // Specific time chip
                val isSpecificTime = state.reminderInterval is ReminderInterval.FixedTime
                FilterChip(
                    selected = isSpecificTime,
                    onClick = {
                        if (!isSpecificTime) {
                            onAction(
                                StreakEditorAction.OnReminderSelect(
                                    ReminderInterval.FixedTime(hour = 9, minute = 0),
                                ),
                            )
                        }
                    },
                    label = { Text(stringResource(R.string.editor_reminder_specific_time)) },
                )
            }

            // Active window picker when periodic reminder is selected
            AnimatedVisibility(visible = state.reminderInterval is ReminderInterval.Periodic) {
                val periodic = state.reminderInterval as? ReminderInterval.Periodic
                val fromTimeState = rememberTimePickerState(
                    initialHour = periodic?.fromHour ?: 8,
                    initialMinute = 0,
                    is24Hour = true,
                )
                val toTimeState = rememberTimePickerState(
                    initialHour = periodic?.toHour ?: 22,
                    initialMinute = 0,
                    is24Hour = true,
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.editor_reminder_active_from),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            TimeInput(state = fromTimeState)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.editor_reminder_active_to),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            TimeInput(state = toTimeState)
                        }
                    }

                    LaunchedEffect(fromTimeState.hour) {
                        val p = (state.reminderInterval as? ReminderInterval.Periodic) ?: return@LaunchedEffect
                        if (fromTimeState.hour != p.fromHour) {
                            onAction(
                                StreakEditorAction.OnReminderSelect(
                                    p.copy(fromHour = fromTimeState.hour),
                                ),
                            )
                        }
                    }
                    LaunchedEffect(toTimeState.hour) {
                        val p = (state.reminderInterval as? ReminderInterval.Periodic) ?: return@LaunchedEffect
                        if (toTimeState.hour != p.toHour) {
                            onAction(
                                StreakEditorAction.OnReminderSelect(
                                    p.copy(toHour = toTimeState.hour),
                                ),
                            )
                        }
                    }
                }
            }

            // Time picker when specific time is selected
            AnimatedVisibility(visible = state.reminderInterval is ReminderInterval.FixedTime) {
                val fixedTime = state.reminderInterval as? ReminderInterval.FixedTime
                val timePickerState = rememberTimePickerState(
                    initialHour = fixedTime?.hour ?: 9,
                    initialMinute = fixedTime?.minute ?: 0,
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    TimePicker(
                        state = timePickerState,
                    )

                    // Observe time changes
                    LaunchedEffect(timePickerState.hour, timePickerState.minute) {
                        val ft = (state.reminderInterval as? ReminderInterval.FixedTime) ?: return@LaunchedEffect
                        if (timePickerState.hour != ft.hour || timePickerState.minute != ft.minute) {
                            onAction(
                                StreakEditorAction.OnReminderSelect(
                                    ReminderInterval.FixedTime(
                                        hour = timePickerState.hour,
                                        minute = timePickerState.minute,
                                    ),
                                ),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Save button
            Button(
                onClick = { onAction(StreakEditorAction.OnSaveClick) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSaving,
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(
                        text = if (state.isEditing) {
                            stringResource(R.string.editor_save_changes)
                        } else {
                            stringResource(R.string.editor_create_streak)
                        },
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier,
    )
}

@Composable
private fun StreakFrequency.toDisplayString(): String {
    return when (this) {
        StreakFrequency.DAILY -> stringResource(R.string.editor_frequency_daily)
        StreakFrequency.WEEKDAYS -> stringResource(R.string.editor_frequency_weekdays)
        StreakFrequency.WEEKENDS -> stringResource(R.string.editor_frequency_weekends)
        StreakFrequency.CUSTOM -> stringResource(R.string.editor_frequency_custom)
    }
}

@Composable
private fun DayOfWeek.toAbbreviated(): String {
    return when (this) {
        DayOfWeek.MONDAY -> stringResource(R.string.day_mon)
        DayOfWeek.TUESDAY -> stringResource(R.string.day_tue)
        DayOfWeek.WEDNESDAY -> stringResource(R.string.day_wed)
        DayOfWeek.THURSDAY -> stringResource(R.string.day_thu)
        DayOfWeek.FRIDAY -> stringResource(R.string.day_fri)
        DayOfWeek.SATURDAY -> stringResource(R.string.day_sat)
        DayOfWeek.SUNDAY -> stringResource(R.string.day_sun)
        else -> ""
    }
}
