package com.hisuperaman.wallety.data.model

enum class BackupFrequency(val label: String, val value: Long?) {
    DAILY("Daily", 1),
    WEEKLY("Weekly", 7),
    MONTHLY("Monthly", 28),
    ONLY_WHEN_I_CLICK_BACKUP("Only when I tap 'Back up'", null),
}