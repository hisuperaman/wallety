package com.hisuperaman.wallety.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hisuperaman.wallety.R
import com.hisuperaman.wallety.data.formatFileSize
import com.hisuperaman.wallety.data.getFormattedTimestamp
import com.hisuperaman.wallety.data.model.BackupFrequency
import com.hisuperaman.wallety.ui.components.ActionButton
import com.hisuperaman.wallety.ui.components.ConfirmationDialog
import com.hisuperaman.wallety.ui.screens.settings.components.SettingsItem
import com.hisuperaman.wallety.ui.screens.settings.components.SiwgButton
import com.hisuperaman.wallety.ui.viewmodel.BackupScheduleEvent
import com.hisuperaman.wallety.ui.viewmodel.BackupScheduleViewModel
import com.hisuperaman.wallety.ui.viewmodel.DriveEvent
import com.hisuperaman.wallety.ui.viewmodel.DriveViewModel


@Composable
fun AutoBackupDialog(
    onDismiss: () -> Unit,
    showDialog: Boolean = false,
    selected: BackupFrequency,
    onSelect: (BackupFrequency) -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Choose Backup Frequency") },
            text = {
                Column {
                    BackupFrequency.entries.map { backupFrequency ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                            .clickable { onSelect(backupFrequency) }
                        ) {
                            RadioButton(
                                selected = backupFrequency == selected,
                                onClick = { onSelect(backupFrequency) }
                            )
                            Text(backupFrequency.label)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("OK")
                }
            },
        )
    }
}

@Composable
fun BackupRestoreScreen(
    modifier: Modifier = Modifier,
    driveViewModel: DriveViewModel,
    backupScheduleViewModel: BackupScheduleViewModel
) {
    val webClientId = stringResource(R.string.web_client_id)
    val driveState by driveViewModel.state.collectAsState()
    val backupScheduleState by backupScheduleViewModel.state.collectAsState()
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(horizontal = 8.dp)
        ) {
            Column {
                Text(
                    text = "Backup settings",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Back up your transactions and balance to your Google Account's storage. You can restore them on a new phone after you download Wallety on it.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            if (driveState.userEmail != null) {
                Column {
                    Text(
                        text = ("Last Backup: " + getFormattedTimestamp(driveState.lastBackupTimestamp))
                    )
                    Text(
                        text = "Size: ${driveState.lastBackupSize?.let { formatFileSize(it.toLong()) }}"
                    )
                }

                Row {
                    ActionButton(
                        text = "Back up",
                        onClick = {driveViewModel.onEvent(DriveEvent.ShowBackupDialog)},
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    ActionButton(
                        text = "Restore",
                        outline = true,
                        onClick = {driveViewModel.onEvent(DriveEvent.ShowRestoreDialog)},
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        SiwgButton(
            webClientId = webClientId,
            onEvent = driveViewModel::onEvent,
            state = driveState
        )

        if (driveState.userEmail != null) {
            SettingsItem(
                title = stringResource(R.string.automatic_backups),
                description = stringResource(R.string.automatic_backups_desc),
                onClick = { driveViewModel.onEvent(DriveEvent.ShowAutoBackupDialog) }
            )
        }
    }

    if (driveState.backupLoading || driveState.restoreLoading) {
        Dialog(onDismissRequest = {}) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }

    ConfirmationDialog(
        showDialog = driveState.isBackupDialogVisible,
        title = "Backup",
        message = "Creating a new backup will replace the existing one. Do you want to continue?",
        onConfirm = {
            driveViewModel.onEvent(DriveEvent.Backup)
            driveViewModel.onEvent(DriveEvent.HideBackupDialog)
        },
        onDismiss = {driveViewModel.onEvent(DriveEvent.HideBackupDialog)}
    )

    ConfirmationDialog(
        showDialog = driveState.isRestoreDialogVisible,
        title = "Restore",
        message = "Restoring will overwrite your current data. Do you want to continue?",
        onConfirm = {
            driveViewModel.onEvent(DriveEvent.Restore)
            driveViewModel.onEvent(DriveEvent.HideRestoreDialog)
        },
        onDismiss = {driveViewModel.onEvent(DriveEvent.HideRestoreDialog)}
    )

    AutoBackupDialog(
        showDialog = driveState.isAutoBackupDialogVisible,
        onDismiss = {driveViewModel.onEvent(DriveEvent.HideAutoBackupDialog)},
        selected = backupScheduleState.backupFrequency,
        onSelect = { backupScheduleViewModel.onEvent(BackupScheduleEvent.SaveSchedule(context, it)) }
    )
}
