package com.kiddostreak.feature.streak.data.di

import androidx.datastore.core.DataStore
import com.kiddostreak.core.data.datastore.KiddoDataStoreFactory
import com.kiddostreak.feature.streak.data.DataStoreStreakRepository
import com.kiddostreak.feature.streak.data.dto.StreakDataStoreModel
import com.kiddostreak.feature.streak.data.serializer.StreakDataStoreSerializer
import com.kiddostreak.feature.streak.domain.repository.StreakRepository
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val streakDataModule = module {
    single<DataStore<StreakDataStoreModel>>(named("streaks")) {
        KiddoDataStoreFactory.create(
            context = get(),
            fileName = "streaks.json",
            serializer = StreakDataStoreSerializer
        )
    }

    single { DataStoreStreakRepository(get(named("streaks"))) } bind StreakRepository::class
}
