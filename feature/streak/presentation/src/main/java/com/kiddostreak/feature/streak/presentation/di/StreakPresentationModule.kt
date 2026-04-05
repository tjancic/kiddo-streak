package com.kiddostreak.feature.streak.presentation.di

import com.kiddostreak.feature.streak.domain.usecase.ArchiveStreakUseCase
import com.kiddostreak.feature.streak.domain.usecase.CompleteStreakUseCase
import com.kiddostreak.feature.streak.domain.usecase.CreateStreakUseCase
import com.kiddostreak.feature.streak.domain.usecase.GetActiveStreaksWithStatsUseCase
import com.kiddostreak.feature.streak.domain.usecase.GetStreakCalendarUseCase
import com.kiddostreak.feature.streak.domain.usecase.GetStreakStatsUseCase
import com.kiddostreak.feature.streak.domain.usecase.UncompleteStreakUseCase
import com.kiddostreak.feature.streak.presentation.detail.StreakDetailViewModel
import com.kiddostreak.feature.streak.presentation.editor.StreakEditorViewModel
import com.kiddostreak.feature.streak.presentation.home.HomeViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val streakPresentationModule = module {
    // Use cases
    factoryOf(::GetActiveStreaksWithStatsUseCase)
    factoryOf(::GetStreakStatsUseCase)
    factoryOf(::GetStreakCalendarUseCase)
    factoryOf(::CreateStreakUseCase)
    factoryOf(::CompleteStreakUseCase)
    factoryOf(::UncompleteStreakUseCase)
    factoryOf(::ArchiveStreakUseCase)

    // ViewModels
    viewModelOf(::HomeViewModel)
    viewModelOf(::StreakEditorViewModel)
    viewModelOf(::StreakDetailViewModel)
}
