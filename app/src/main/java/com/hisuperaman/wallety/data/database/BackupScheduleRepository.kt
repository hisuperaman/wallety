package com.hisuperaman.wallety.data.database

import com.hisuperaman.wallety.data.model.BackupSchedule
import javax.inject.Inject

class BackupScheduleRepository @Inject constructor(private val backupScheduleDao: BackupScheduleDao) {
    suspend fun getBackupSchedule() = backupScheduleDao.getBackupSchedule()

    suspend fun saveBackupSchedule(schedule: BackupSchedule) {
        backupScheduleDao.saveBackupSchedule(schedule)
    }
}