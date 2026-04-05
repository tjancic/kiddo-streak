package com.kiddostreak.feature.settings.data

import androidx.datastore.core.DataStore
import com.kiddostreak.feature.settings.data.dto.AppSettingsDto
import com.kiddostreak.feature.settings.data.mapper.toAppSettings
import com.kiddostreak.feature.settings.data.mapper.toDto
import com.kiddostreak.feature.settings.domain.model.AppSettings
import com.kiddostreak.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreSettingsRepository(
    private val dataStore: DataStore<AppSettingsDto>,
) : SettingsRepository {

    override fun getSettings(): Flow<AppSettings> {
        return dataStore.data.map { it.toAppSettings() }
    }

    override suspend fun updateSettings(settings: AppSettings) {
        dataStore.updateData { settings.toDto() }
    }
}
