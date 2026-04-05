package com.kiddostreak.feature.streak.domain.error

import com.kiddostreak.core.domain.Error

enum class StreakValidationError : Error {
    EMPTY_NAME,
    NAME_TOO_LONG,
    INVALID_COLOR,
    CUSTOM_DAYS_EMPTY,
}
