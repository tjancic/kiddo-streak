package com.kiddostreak.feature.streak.domain.model

enum class Milestone(val days: Int) {
    WEEK(7),
    TWO_WEEKS(14),
    MONTH(30),
    QUARTER(90),
    HUNDRED(100),
    YEAR(365);

    companion object {
        fun forDay(day: Int): Milestone? = entries.firstOrNull { it.days == day }
        fun nextAfter(day: Int): Milestone? = entries.firstOrNull { it.days > day }
    }
}
