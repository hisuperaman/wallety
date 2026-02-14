package com.hisuperaman.wallety.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.hisuperaman.wallety.data.calculateInitialDelay
import com.hisuperaman.wallety.data.database.BackupScheduleRepository
import com.hisuperaman.wallety.data.model.BackupFrequency
import com.hisuperaman.wallety.data.model.BackupSchedule
import com.hisuperaman.wallety.workers.BackupWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class BackupScheduleViewModel @Inject constructor(
    private val repository: BackupScheduleRepository
) : ViewModel() {
    private val _state = MutableStateFlow(BackupScheduleState())
    val state: StateFlow<BackupScheduleState> = _state

    init {
        viewModelScope.launch {
            val backupSchedule = repository.getBackupSchedule()
            if (backupSchedule != null)
                _state.update { it.copy(backupFrequency = backupSchedule.backupFrequency) }
        }
    }

    fun onEvent(event: BackupScheduleEvent) {
        when (event) {
            is BackupScheduleEvent.SaveSchedule -> saveSchedule(
                event.context,
                event.backupFrequency
            )
        }
    }

    fun saveSchedule(context: Context, backupFrequency: BackupFrequency) {
        viewModelScope.launch {
            val schedule = BackupSchedule(id = 1, backupFrequency = backupFrequency)
            repository.saveBackupSchedule(schedule)
            applySchedule(context, schedule)
            _state.update { it.copy(backupFrequency = schedule.backupFrequency) }
        }
    }

    private fun applySchedule(context: Context, schedule: BackupSchedule) {
        val wm = WorkManager.getInstance(context)

        if (schedule.backupFrequency == BackupFrequency.ONLY_WHEN_I_CLICK_BACKUP || schedule.backupFrequency.value == null) {
            wm.cancelUniqueWork("backup_schedule")
            return
        }

        val delay = calculateInitialDelay(12, 0)

        val request = PeriodicWorkRequestBuilder<BackupWorker>(
            schedule.backupFrequency.value, TimeUnit.DAYS
        )
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        wm.enqueueUniquePeriodicWork(
            "backup_schedule",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}