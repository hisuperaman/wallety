package com.hisuperaman.wallety.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "backup_schedule")
data class BackupSchedule(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val backupFrequency: BackupFrequency
)
