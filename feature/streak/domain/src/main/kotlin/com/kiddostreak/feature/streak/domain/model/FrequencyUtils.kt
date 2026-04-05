package com.kiddostreak.feature.streak.domain.model

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.datetime.minus

fun isEligibleDay(
    frequency: StreakFrequency,
    date: LocalDate,
    customDays: Set<DayOfWeek> = emptySet(),
): Boolean {
    return when (frequency) {
        StreakFrequency.DAILY -> true
        StreakFrequency.WEEKDAYS -> date.dayOfWeek != DayOfWeek.SATURDAY && date.dayOfWeek != DayOfWeek.SUNDAY
        StreakFrequency.WEEKENDS -> date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY
        StreakFrequency.CUSTOM -> date.dayOfWeek in customDays
    }
}

fun nextEligibleDay(
    frequency: StreakFrequency,
    from: LocalDate,
    customDays: Set<DayOfWeek> = emptySet(),
): LocalDate {
    var date = from
    while (!isEligibleDay(frequency, date, customDays)) {
        date = date.plus(1, DateTimeUnit.DAY)
    }
    return date
}
