package com.kiddostreak.feature.streak.presentation.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kiddostreak.core.designsystem.components.ContributionGrid
import com.kiddostreak.core.designsystem.components.KiddoStreakTopBar
import com.kiddostreak.core.designsystem.components.MilestoneProgressBar
import com.kiddostreak.core.designsystem.components.parseHexColor
import com.kiddostreak.core.presentation.ObserveAsEvents
import com.kiddostreak.feature.widget.WidgetUpdater
import kotlinx.coroutines.launch
import com.kiddostreak.feature.streak.presentation.R
import com.kiddostreak.feature.streak.presentation.model.StreakStatsUi
import org.koin.androidx.compose.koinViewModel

@Composable
fun StreakDetailRoot(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: StreakDetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            StreakDetailEvent.NavigateBack -> onNavigateBack()
            is StreakDetailEvent.NavigateToEdit -> onNavigateToEdit(event.streakId)
            is StreakDetailEvent.ShowSnackbar -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = event.message.asString(context),
                    )
                }
            }

            StreakDetailEvent.WidgetUpdateNeeded -> {
                WidgetUpdater.updateAll(context)
            }
        }
    }

    StreakDetailScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction,
    )
}

@Composable
fun StreakDetailScreen(
    state: StreakDetailState,
    snackbarHostState: SnackbarHostState,
    onAction: (StreakDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            KiddoStreakTopBar(
                title = state.streakName,
                onBackClick = { onAction(StreakDetailAction.OnBackClick) },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(R.string.detail_more_options),
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.detail_edit)) },
                                onClick = {
                                    showMenu = false
                                    onAction(StreakDetailAction.OnEditClick)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                    )
                                },
                            )
                            if (!state.isPrimary) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.detail_set_as_main)) },
                                    onClick = {
                                        showMenu = false
                                        onAction(StreakDetailAction.OnSetAsPrimaryClick)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                        )
                                    },
                                )
                            }
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.detail_archive)) },
                                onClick = {
                                    showMenu = false
                                    onAction(StreakDetailAction.OnArchiveClick)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Archive,
                                        contentDescription = null,
                                    )
                                },
                            )
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        AnimatedVisibility(
            visible = state.isLoading,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        AnimatedVisibility(
            visible = !state.isLoading,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            val streakColor = remember(state.streakColorHex) { parseHexColor(state.streakColorHex) }
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState),
            ) {
                // Hero section
                HeroSection(
                    emoji = state.streakEmoji,
                    streakColor = streakColor,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Stats grid
                if (state.stats != null) {
                    StatsGrid(
                        stats = state.stats,
                        streakColor = streakColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Milestone section
                    MilestoneSection(
                        stats = state.stats,
                        streakColor = streakColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Calendar section
                Text(
                    text = stringResource(R.string.detail_activity),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                ContributionGrid(
                    completedDates = state.completedDates,
                    streakColor = streakColor,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Check-in button
                Button(
                    onClick = { onAction(StreakDetailAction.OnCheckInClick) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.isCompletedToday) {
                            MaterialTheme.colorScheme.surfaceVariant
                        } else {
                            streakColor
                        },
                        contentColor = if (state.isCompletedToday) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            Color.White
                        },
                    ),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    if (state.isCompletedToday) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.detail_completed),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.detail_check_in),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun HeroSection(
    emoji: String,
    streakColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(160.dp)
            .background(
                color = streakColor.copy(alpha = 0.12f),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = emoji,
            fontSize = 72.sp,
        )
    }
}

@Composable
private fun StatsGrid(
    stats: StreakStatsUi,
    streakColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatCard(
                label = stringResource(R.string.detail_current_streak),
                value = stats.currentStreak,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Whatshot,
                        contentDescription = null,
                        tint = streakColor,
                        modifier = Modifier.size(20.dp),
                    )
                },
                modifier = Modifier.weight(1f),
            )
            StatCard(
                label = stringResource(R.string.detail_longest_streak),
                value = stats.longestStreak,
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatCard(
                label = stringResource(R.string.detail_total_days),
                value = stats.totalCompletions,
                modifier = Modifier.weight(1f),
            )
            StatCard(
                label = stringResource(R.string.detail_completion_rate),
                value = stats.completionRate,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (icon != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    icon()
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun MilestoneSection(
    stats: StreakStatsUi,
    streakColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.detail_milestone),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )

            if (stats.nextMilestone != null) {
                Text(
                    text = stringResource(R.string.detail_next_milestone, stats.nextMilestone),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (stats.currentMilestone != null) {
            Text(
                text = stringResource(R.string.detail_current_milestone_label, stats.currentMilestone),
                style = MaterialTheme.typography.bodyMedium,
                color = streakColor,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        MilestoneProgressBar(
            currentDays = stats.currentStreak.filter { it.isDigit() }.toIntOrNull() ?: 0,
            streakColor = streakColor,
            nextMilestoneDays = stats.nextMilestoneDays,
        )
    }
}
