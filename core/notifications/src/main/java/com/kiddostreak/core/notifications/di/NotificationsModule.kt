package com.kiddostreak.core.notifications.di

import com.kiddostreak.core.notifications.ReminderScheduler
import com.kiddostreak.feature.streak.domain.scheduler.ReminderSchedulerPort
import com.kiddostreak.feature.streak.domain.usecase.SyncReminderScheduleUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val notificationsModule = module {
    singleOf(::ReminderScheduler) bind ReminderSchedulerPort::class
    factoryOf(::SyncReminderScheduleUseCase)
}
