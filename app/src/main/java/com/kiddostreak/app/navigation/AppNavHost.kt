package com.kiddostreak.app.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.kiddostreak.feature.settings.presentation.navigation.SettingsRoute
import com.kiddostreak.feature.settings.presentation.navigation.settingsGraph
import com.kiddostreak.feature.streak.presentation.navigation.HomeRoute
import com.kiddostreak.feature.streak.presentation.navigation.streakGraph

@Composable
fun AppNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier.padding(innerPadding),
    ) {
        streakGraph(
            navController = navController,
            onNavigateToSettings = {
                navController.navigate(SettingsRoute)
            },
        )

        settingsGraph(navController = navController)
    }
}
