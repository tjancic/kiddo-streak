package com.kiddostreak.core.presentation

import com.kiddostreak.core.domain.DataError

fun DataError.Local.toUiText(): UiText {
    return when (this) {
        DataError.Local.DISK_FULL -> UiText.StringResource(R.string.error_disk_full)
        DataError.Local.NOT_FOUND -> UiText.StringResource(R.string.error_not_found)
        DataError.Local.UNKNOWN -> UiText.StringResource(R.string.error_unknown)
    }
}
