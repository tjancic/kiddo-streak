package com.kiddostreak.feature.settings.data.serializer

import com.kiddostreak.core.data.datastore.JsonDataStoreSerializer
import com.kiddostreak.feature.settings.data.dto.AppSettingsDto

object SettingsDataStoreSerializer : JsonDataStoreSerializer<AppSettingsDto>(
    defaultValue = AppSettingsDto(),
    serializer = AppSettingsDto.serializer(),
)
