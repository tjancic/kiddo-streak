package com.kiddostreak.app.di

import com.kiddostreak.core.notifications.di.notificationsModule
import com.kiddostreak.feature.settings.data.di.settingsDataModule
import com.kiddostreak.feature.settings.presentation.di.settingsPresentationModule
import com.kiddostreak.feature.streak.data.di.streakDataModule
import com.kiddostreak.feature.streak.presentation.di.streakPresentationModule
import com.kiddostreak.feature.widget.di.widgetModule

val appModules = listOf(
    streakDataModule,
    streakPresentationModule,
    settingsDataModule,
    settingsPresentationModule,
    notificationsModule,
    widgetModule,
)
