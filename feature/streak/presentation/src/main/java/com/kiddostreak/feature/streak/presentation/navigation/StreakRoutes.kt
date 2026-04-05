package com.kiddostreak.feature.streak.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data class StreakDetailRoute(val streakId: String)

@Serializable
data class StreakEditorRoute(val streakId: String? = null)
