package com.kiddostreak.core.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer

object KiddoDataStoreFactory {
    fun <T> create(
        context: Context,
        fileName: String,
        serializer: Serializer<T>
    ): DataStore<T> = DataStoreFactory.create(
        serializer = serializer,
        produceFile = { context.filesDir.resolve(fileName) }
    )
}
