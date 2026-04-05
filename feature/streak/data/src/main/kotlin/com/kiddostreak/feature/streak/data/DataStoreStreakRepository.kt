package com.kiddostreak.feature.streak.data

import androidx.datastore.core.DataStore
import com.kiddostreak.core.domain.DataError
import com.kiddostreak.core.domain.EmptyResult
import com.kiddostreak.core.domain.Result
import com.kiddostreak.feature.streak.data.dto.StreakDataStoreModel
import com.kiddostreak.feature.streak.data.mapper.toCompletion
import com.kiddostreak.feature.streak.data.mapper.toDto
import com.kiddostreak.feature.streak.data.mapper.toStreak
import com.kiddostreak.feature.streak.domain.model.Streak
import com.kiddostreak.feature.streak.domain.model.StreakCompletion
import com.kiddostreak.feature.streak.domain.repository.StreakRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlin.coroutines.cancellation.CancellationException

class DataStoreStreakRepository(
    private val dataStore: DataStore<StreakDataStoreModel>
) : StreakRepository {

    override fun getActiveStreaks(): Flow<List<Streak>> {
        return dataStore.data.map { store ->
            store.streaks
                .filter { !it.isArchived }
                .sortedByDescending { it.isPrimary }
                .map { it.toStreak() }
        }
    }

    override fun getStreakById(id: String): Flow<Streak?> {
        return dataStore.data.map { store ->
            store.streaks
                .firstOrNull { it.id == id }
                ?.toStreak()
        }
    }

    override fun getCompletionsForStreak(streakId: String): Flow<List<StreakCompletion>> {
        return dataStore.data.map { store ->
            store.completions
                .filter { it.streakId == streakId }
                .map { it.toCompletion() }
        }
    }

    override fun getCompletionsForDate(date: LocalDate): Flow<List<StreakCompletion>> {
        val dateString = date.toString()
        return dataStore.data.map { store ->
            store.completions
                .filter { it.date == dateString }
                .map { it.toCompletion() }
        }
    }

    override fun getAllCompletions(): Flow<List<StreakCompletion>> {
        return dataStore.data.map { store ->
            store.completions.map { it.toCompletion() }
        }
    }

    override suspend fun createStreak(streak: Streak): EmptyResult<DataError.Local> {
        return updateDataSafely { store ->
            store.copy(streaks = store.streaks + streak.toDto())
        }
    }

    override suspend fun updateStreak(streak: Streak): EmptyResult<DataError.Local> {
        return updateDataSafely { store ->
            store.copy(
                streaks = store.streaks.map { dto ->
                    if (dto.id == streak.id) streak.toDto() else dto
                }
            )
        }
    }

    override suspend fun archiveStreak(streakId: String): EmptyResult<DataError.Local> {
        return updateDataSafely { store ->
            store.copy(
                streaks = store.streaks.map { dto ->
                    if (dto.id == streakId) dto.copy(isArchived = true) else dto
                }
            )
        }
    }

    override suspend fun completeStreak(
        streakId: String,
        date: LocalDate
    ): EmptyResult<DataError.Local> {
        return updateDataSafely { store ->
            val dateString = date.toString()
            val alreadyCompleted = store.completions.any {
                it.streakId == streakId && it.date == dateString
            }
            if (alreadyCompleted) {
                store
            } else {
                val completionDto = StreakCompletion(
                    streakId = streakId,
                    date = date,
                    completedAt = Clock.System.now()
                ).toDto()
                store.copy(completions = store.completions + completionDto)
            }
        }
    }

    override suspend fun uncompleteStreak(
        streakId: String,
        date: LocalDate
    ): EmptyResult<DataError.Local> {
        return updateDataSafely { store ->
            val dateString = date.toString()
            store.copy(
                completions = store.completions.filter {
                    !(it.streakId == streakId && it.date == dateString)
                }
            )
        }
    }

    override suspend fun setPrimaryStreak(streakId: String): EmptyResult<DataError.Local> {
        return updateDataSafely { store ->
            store.copy(
                streaks = store.streaks.map { dto ->
                    dto.copy(isPrimary = dto.id == streakId)
                }
            )
        }
    }

    private suspend fun updateDataSafely(
        transform: (StreakDataStoreModel) -> StreakDataStoreModel
    ): EmptyResult<DataError.Local> {
        return try {
            dataStore.updateData { store -> transform(store) }
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            Result.Failure(DataError.Local.UNKNOWN)
        }
    }
}
