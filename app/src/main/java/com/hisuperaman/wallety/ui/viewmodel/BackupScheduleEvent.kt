package com.hisuperaman.wallety.ui.viewmodel

import android.content.Context
import com.hisuperaman.wallety.data.model.BackupFrequency

sealed interface BackupScheduleEvent {
    data class SaveSchedule(val context: Context, val backupFrequency: BackupFrequency) : BackupScheduleEvent
}