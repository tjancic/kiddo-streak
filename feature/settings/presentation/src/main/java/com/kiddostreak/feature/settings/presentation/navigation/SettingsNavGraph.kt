package com.kiddostreak.feature.settings.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kiddostreak.feature.settings.presentation.SettingsScreenRoot

fun NavGraphBuilder.settingsGraph(navController: NavController) {
    composable<SettingsRoute> {
        SettingsScreenRoot(
            onNavigateBack = { navController.popBackStack() },
        )
    }
}
