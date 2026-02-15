package com.hisuperaman.wallety.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.hisuperaman.wallety.R
import com.hisuperaman.wallety.data.model.ThemeMode
import com.hisuperaman.wallety.data.toRupees
import com.hisuperaman.wallety.ui.components.InputConfirmationDialog
import com.hisuperaman.wallety.ui.components.ToastManager
import com.hisuperaman.wallety.ui.screens.settings.components.SettingsItem
import com.hisuperaman.wallety.ui.viewmodel.AccountEvent
import com.hisuperaman.wallety.ui.viewmodel.AccountViewModel
import com.hisuperaman.wallety.ui.viewmodel.BackupScheduleViewModel
import com.hisuperaman.wallety.ui.viewmodel.DriveEvent
import com.hisuperaman.wallety.ui.viewmodel.DriveViewModel
import androidx.core.net.toUri


@Composable
fun ThemeDialog(
    themeMode: ThemeMode,
    onDismiss: () -> Unit,
    showDialog: Boolean = false,
    onSelect: (ThemeMode) -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Choose Theme") },
            text = {
                Column {
                    ThemeMode.entries.forEach { mode ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(mode) }
                        ) {
                            RadioButton(
                                selected = themeMode == mode,
                                onClick = { onSelect(mode) }
                            )
                            Text(mode.label)
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
fun SettingsScreen(
    accountViewModel: AccountViewModel,
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    onBackupRestoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accountState by accountViewModel.state.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
    ) {
        SettingsItem(
            imageVector = Icons.Outlined.AccountBalanceWallet,
            title = stringResource(R.string.edit_base_balance),
            description = stringResource(R.string.edit_base_balance_desc),
            onClick = {accountViewModel.onEvent(AccountEvent.ShowDialog)}
        )
        SettingsItem(
            imageVector = Icons.Outlined.WbSunny,
            title = stringResource(R.string.theme),
            description = stringResource(R.string.theme_desc),
            onClick = {showThemeDialog = true}
        )
        SettingsItem(
            imageVector = Icons.Outlined.Backup,
            title = stringResource(R.string.backup),
            description = stringResource(R.string.backup_desc),
            onClick = { onBackupRestoreClick() }
        )
        SettingsItem(
            imageVector = Icons.Default.PrivacyTip,
            title = "Privacy Policy",
            description = "Read privacy policy.",
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, "https://hisuperaman.me/wallety/privacy-policy.html".toUri())
                context.startActivity(intent)
            }
        )
        SettingsItem(
            imageVector = Icons.AutoMirrored.Outlined.InsertDriveFile,
            title = "Terms and Conditions",
            description = "Read terms and conditions.",
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, "https://hisuperaman.me/wallety/terms-and-conditions.html".toUri())
                context.startActivity(intent)
            }
        )
        SettingsItem(
            imageVector = Icons.Outlined.Mail,
            title = "Contact",
            description = stringResource(R.string.contact_desc),
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, "https://hisuperaman.me/wallety/support.html".toUri())
                context.startActivity(intent)
            }
        )
    }

    ThemeDialog(
        themeMode = themeMode,
        onSelect = onThemeChange,
        showDialog = showThemeDialog,
        onDismiss = {showThemeDialog = false}
    )

    InputConfirmationDialog(
        showDialog = accountState.isEditingBalance,
        title = "Base Balance",
        message = "Edit base balance",
        placeholder = "Write new base balance here",
        onConfirm = { input ->
            accountViewModel.onEvent(AccountEvent.SaveAccount(input))
            accountViewModel.onEvent(AccountEvent.HideDialog)
        },
        inputText = accountState.account?.balance?.toRupees().toString(),
        onDismiss = { accountViewModel.onEvent(AccountEvent.HideDialog) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}
