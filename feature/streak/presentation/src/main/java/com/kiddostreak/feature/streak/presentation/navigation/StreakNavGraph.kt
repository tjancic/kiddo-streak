package com.kiddostreak.feature.streak.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.kiddostreak.feature.streak.presentation.detail.StreakDetailRoot
import com.kiddostreak.feature.streak.presentation.editor.StreakEditorRoot
import com.kiddostreak.feature.streak.presentation.home.HomeRoot

fun NavGraphBuilder.streakGraph(
    navController: NavController,
    onNavigateToSettings: () -> Unit,
) {
    composable<HomeRoute> {
        HomeRoot(
            onNavigateToStreakDetail = { streakId ->
                navController.navigate(StreakDetailRoute(streakId))
            },
            onNavigateToCreateStreak = {
                navController.navigate(StreakEditorRoute())
            },
            onNavigateToSettings = onNavigateToSettings,
        )
    }

    composable<StreakDetailRoute> {
        StreakDetailRoot(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToEdit = { streakId ->
                navController.navigate(StreakEditorRoute(streakId))
            },
        )
    }

    composable<StreakEditorRoute> {
        StreakEditorRoot(
            onNavigateBack = { navController.popBackStack() },
        )
    }
}
