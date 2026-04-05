package com.kiddostreak.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.kiddostreak.app.navigation.AppNavHost

@Composable
fun KiddoStreakApp() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            innerPadding = innerPadding,
        )
    }
}
