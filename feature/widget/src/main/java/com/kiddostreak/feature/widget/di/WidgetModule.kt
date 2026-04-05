package com.kiddostreak.feature.widget.di

import org.koin.dsl.module

val widgetModule = module {
    // Widget components use KoinJavaComponent.getKoin() directly
    // since GlanceAppWidget and ActionCallback are instantiated by the system.
    // This module is included for consistency and future extensibility.
}
