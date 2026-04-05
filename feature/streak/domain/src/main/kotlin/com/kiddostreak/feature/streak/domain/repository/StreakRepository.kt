package com.kiddostreak.feature.streak.domain.repository

import com.kiddostreak.core.domain.DataError
import com.kiddostreak.core.domain.EmptyResult
import com.kiddostreak.feature.streak.domain.model.Streak
import com.kiddostreak.feature.streak.domain.model.StreakCompletion
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface StreakRepository {
    fun getActiveStreaks(): Flow<List<Streak>>
    fun getStreakById(id: String): Flow<Streak?>
    fun getCompletionsForStreak(streakId: String): Flow<List<StreakCompletion>>
    fun getCompletionsForDate(date: LocalDate): Flow<List<StreakCompletion>>
    fun getAllCompletions(): Flow<List<StreakCompletion>>
    suspend fun createStreak(streak: Streak): EmptyResult<DataError.Local>
    suspend fun updateStreak(streak: Streak): EmptyResult<DataError.Local>
    suspend fun archiveStreak(streakId: String): EmptyResult<DataError.Local>
    suspend fun completeStreak(streakId: String, date: LocalDate): EmptyResult<DataError.Local>
    suspend fun uncompleteStreak(streakId: String, date: LocalDate): EmptyResult<DataError.Local>
    suspend fun setPrimaryStreak(streakId: String): EmptyResult<DataError.Local>
}
