package com.kiddostreak.feature.streak.domain.usecase

import com.kiddostreak.core.domain.DataError
import com.kiddostreak.core.domain.EmptyResult
import com.kiddostreak.feature.streak.domain.repository.StreakRepository

class ArchiveStreakUseCase(
    private val streakRepository: StreakRepository
) {

    suspend operator fun invoke(streakId: String): EmptyResult<DataError.Local> {
        return streakRepository.archiveStreak(streakId)
    }
}
