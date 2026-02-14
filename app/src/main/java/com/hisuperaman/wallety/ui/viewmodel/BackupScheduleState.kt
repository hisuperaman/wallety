package com.hisuperaman.wallety.ui.viewmodel

import com.hisuperaman.wallety.data.model.BackupFrequency

data class BackupScheduleState(
    val backupFrequency: BackupFrequency = BackupFrequency.ONLY_WHEN_I_CLICK_BACKUP
)