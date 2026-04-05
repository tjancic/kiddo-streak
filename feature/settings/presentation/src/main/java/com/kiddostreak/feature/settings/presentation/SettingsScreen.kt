package com.kiddostreak.feature.settings.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kiddostreak.core.designsystem.components.KiddoStreakTopBar
import com.kiddostreak.core.designsystem.theme.KiddoStreakTheme
import com.kiddostreak.core.presentation.ObserveAsEvents
import com.kiddostreak.feature.streak.domain.model.ReminderInterval
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreenRoot(
    viewModel: SettingsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            SettingsEvent.NavigateBack -> onNavigateBack()
        }
    }

    SettingsScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun SettingsScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
) {
    Scaffold(
        topBar = {
            KiddoStreakTopBar(
                title = stringResource(R.string.settings_title),
                onBackClick = { onAction(SettingsAction.OnBackClick) },
            )
        },
    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
            ) {
                NotificationsSection(
                    remindersEnabled = state.globalRemindersEnabled,
                    reminderInterval = state.defaultReminderInterval,
                    onToggleReminders = { onAction(SettingsAction.OnToggleReminders(it)) },
                    onIntervalChange = { onAction(SettingsAction.OnReminderIntervalChange(it)) },
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                AppearanceSection(
                    useDynamicColors = state.useDynamicColors,
                    showCompletedStreaks = state.showCompletedStreaks,
                    onToggleDynamicColors = { onAction(SettingsAction.OnToggleDynamicColors(it)) },
                    onToggleShowCompleted = { onAction(SettingsAction.OnToggleShowCompleted(it)) },
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                AboutSection()
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp),
    )
}

@Composable
private fun SettingsSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
            )
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
private fun NotificationsSection(
    remindersEnabled: Boolean,
    reminderInterval: ReminderInterval,
    onToggleReminders: (Boolean) -> Unit,
    onIntervalChange: (ReminderInterval) -> Unit,
) {
    SectionHeader(title = stringResource(R.string.settings_section_notifications))

    SettingsSwitch(
        label = stringResource(R.string.settings_enable_reminders),
        description = stringResource(R.string.settings_enable_reminders_desc),
        checked = remindersEnabled,
        onCheckedChange = onToggleReminders,
    )

    AnimatedVisibility(
        visible = remindersEnabled,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        ReminderIntervalSelector(
            selectedInterval = reminderInterval,
            onIntervalChange = onIntervalChange,
        )
    }
}

@Composable
private fun ReminderIntervalSelector(
    selectedInterval: ReminderInterval,
    onIntervalChange: (ReminderInterval) -> Unit,
) {
    val intervals = listOf(
        ReminderInterval.Periodic(2) to stringResource(R.string.settings_interval_2h),
        ReminderInterval.Periodic(4) to stringResource(R.string.settings_interval_4h),
        ReminderInterval.Periodic(12) to stringResource(R.string.settings_interval_12h),
        ReminderInterval.FixedTime(9, 0) to stringResource(R.string.settings_interval_custom),
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        intervals.forEach { (interval, label) ->
            FilterChip(
                selected = isIntervalSelected(selectedInterval, interval),
                onClick = { onIntervalChange(interval) },
                label = { Text(text = label, style = MaterialTheme.typography.labelMedium) },
            )
        }
    }
}

private fun isIntervalSelected(
    current: ReminderInterval,
    candidate: ReminderInterval,
): Boolean = when {
    current is ReminderInterval.Periodic && candidate is ReminderInterval.Periodic ->
        current.hours == candidate.hours

    current is ReminderInterval.FixedTime && candidate is ReminderInterval.FixedTime -> true
    else -> false
}

@Composable
private fun AppearanceSection(
    useDynamicColors: Boolean,
    showCompletedStreaks: Boolean,
    onToggleDynamicColors: (Boolean) -> Unit,
    onToggleShowCompleted: (Boolean) -> Unit,
) {
    SectionHeader(title = stringResource(R.string.settings_section_appearance))

    SettingsSwitch(
        label = stringResource(R.string.settings_dynamic_colors),
        description = stringResource(R.string.settings_dynamic_colors_desc),
        checked = useDynamicColors,
        onCheckedChange = onToggleDynamicColors,
    )

    SettingsSwitch(
        label = stringResource(R.string.settings_show_completed),
        description = stringResource(R.string.settings_show_completed_desc),
        checked = showCompletedStreaks,
        onCheckedChange = onToggleShowCompleted,
    )
}

@Composable
private fun AboutSection() {
    SectionHeader(title = stringResource(R.string.settings_section_about))

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_app_name),
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.settings_version, "1.0.0"),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    Spacer(modifier = Modifier.height(24.dp))
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    KiddoStreakTheme(useDynamicColors = false) {
        SettingsScreen(
            state = SettingsState(isLoading = false),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenLoadingPreview() {
    KiddoStreakTheme(useDynamicColors = false) {
        SettingsScreen(
            state = SettingsState(isLoading = true),
            onAction = {},
        )
    }
}
