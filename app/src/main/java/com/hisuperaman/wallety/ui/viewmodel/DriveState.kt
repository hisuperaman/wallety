package com.hisuperaman.wallety.ui.viewmodel


data class DriveState(
    val userEmail: String? = null,
    val backupLoading: Boolean = false,
    val restoreLoading: Boolean = false,
    val lastBackupTimestamp: String? = null,
    val lastBackupSize: Int? = null,
    val isBackupDialogVisible: Boolean = false,
    val isRestoreDialogVisible: Boolean = false,
    val isAutoBackupDialogVisible: Boolean = false
)