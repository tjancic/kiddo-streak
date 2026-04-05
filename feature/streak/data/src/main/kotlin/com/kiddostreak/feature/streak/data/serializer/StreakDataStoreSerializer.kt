package com.kiddostreak.feature.streak.data.serializer

import com.kiddostreak.core.data.datastore.JsonDataStoreSerializer
import com.kiddostreak.feature.streak.data.dto.StreakDataStoreModel

object StreakDataStoreSerializer : JsonDataStoreSerializer<StreakDataStoreModel>(
    defaultValue = StreakDataStoreModel(),
    serializer = StreakDataStoreModel.serializer(),
)
