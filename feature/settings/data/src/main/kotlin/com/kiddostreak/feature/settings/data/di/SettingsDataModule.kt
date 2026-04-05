package com.kiddostreak.feature.settings.data.di

import androidx.datastore.core.DataStore
import com.kiddostreak.core.data.datastore.KiddoDataStoreFactory
import com.kiddostreak.feature.settings.data.DataStoreSettingsRepository
import com.kiddostreak.feature.settings.data.dto.AppSettingsDto
import com.kiddostreak.feature.settings.data.serializer.SettingsDataStoreSerializer
import com.kiddostreak.feature.settings.domain.repository.SettingsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val settingsDataModule = module {
    single<DataStore<AppSettingsDto>>(named("settings")) {
        KiddoDataStoreFactory.create(
            context = get(),
            fileName = "settings.json",
            serializer = SettingsDataStoreSerializer,
        )
    }

    single { DataStoreSettingsRepository(get(named("settings"))) } bind SettingsRepository::class
}
