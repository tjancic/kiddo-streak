package com.kiddostreak.feature.streak.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class StreakDto(
    val id: String,
    val name: String,
    val emoji: String,
    val colorHex: String,
    val frequency: String,
    val reminderType: String,
    val reminderValue: Int,
    val reminderMinute: Int,
    val createdAt: String,
    val isArchived: Boolean,
    val isPrimary: Boolean = false,
    val customDays: List<String> = emptyList(),
)
