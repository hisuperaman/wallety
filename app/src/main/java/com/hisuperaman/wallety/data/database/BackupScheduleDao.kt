package com.hisuperaman.wallety.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hisuperaman.wallety.data.model.BackupSchedule

@Dao
interface BackupScheduleDao {
    @Query("SELECT * FROM backup_schedule WHERE id = 1")
    suspend fun getBackupSchedule(): BackupSchedule?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBackupSchedule(schedule: BackupSchedule)
}