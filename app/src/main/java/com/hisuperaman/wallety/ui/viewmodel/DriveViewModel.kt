package com.hisuperaman.wallety.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.hisuperaman.wallety.data.database.BackupRestoreHelper
import com.hisuperaman.wallety.data.model.ThemeMode
import com.hisuperaman.wallety.ui.components.ToastManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream


@HiltViewModel
class DriveViewModel @Inject constructor(
    private val helper: BackupRestoreHelper
) : ViewModel() {
    private val _state = MutableStateFlow(DriveState())
    val state: StateFlow<DriveState> = _state
    private var driveService: Drive? = null


    fun checkLogin(context: Context) {
        if (driveService == null) {
            GoogleSignIn.getLastSignedInAccount(context)?.let {
                signUp(it, context)
            }
        }
    }

    suspend fun getBackupInfo(): Pair<String?, Int?> =
        withContext(Dispatchers.IO) {
            try {
                val result = driveService?.files()?.list()
                    ?.setSpaces("appDataFolder")
                    ?.setQ("name='backup.json'")
                    ?.setFields("files(modifiedTime,size)")
                    ?.execute()

                val file = result?.files?.firstOrNull()
                Pair(
                    file?.modifiedTime?.toStringRfc3339(),
                    file?.size
                )
            } catch (e: Exception) {
                Pair(null, null)
            }
        }


    suspend fun updateBackupInfo() {
        val (time, size) = getBackupInfo()
        _state.update {
            it.copy(
                lastBackupTimestamp = time,
                lastBackupSize = size
            )
        }
    }

    fun onEvent(event: DriveEvent) {
        when (event) {
            is DriveEvent.SignUp -> signUp(event.account, event.context)
            DriveEvent.Backup -> backup()
            DriveEvent.Restore -> restore()
            DriveEvent.SignUpFail -> signUpFail()
            DriveEvent.ShowBackupDialog -> showBackupDialog()
            DriveEvent.HideBackupDialog -> hideBackupDialog()
            DriveEvent.ShowRestoreDialog -> showRestoreDialog()
            DriveEvent.HideRestoreDialog -> hideRestoreDialog()
            DriveEvent.ShowAutoBackupDialog -> showAutoBackupDialog()
            DriveEvent.HideAutoBackupDialog -> hideAutoBackupDialog()
        }
    }

    fun showAutoBackupDialog() {
        _state.update {
            it.copy(
                isAutoBackupDialogVisible = true
            )
        }
    }

    fun hideAutoBackupDialog() {
        _state.update {
            it.copy(
                isAutoBackupDialogVisible = false
            )
        }
    }

    fun showBackupDialog() {
        _state.update {
            it.copy(
                isBackupDialogVisible = true
            )
        }
    }

    fun hideBackupDialog() {
        _state.update {
            it.copy(
                isBackupDialogVisible = false
            )
        }
    }

    fun showRestoreDialog() {
        _state.update {
            it.copy(
                isRestoreDialogVisible = true
            )
        }
    }

    fun hideRestoreDialog() {
        _state.update {
            it.copy(
                isRestoreDialogVisible = false
            )
        }
    }

    fun signUp(account: GoogleSignInAccount, context: Context) {
        _state.update {
            it.copy(
                userEmail = account.email
            )
        }
        val credential = GoogleAccountCredential.usingOAuth2(
            context,
            listOf(DriveScopes.DRIVE_APPDATA)
        )
        credential.selectedAccount = account.account
        driveService = Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("Wallety").build()

        viewModelScope.launch {
            updateBackupInfo()
        }
    }

    fun signUpFail() {
        _state.update {
            it.copy(
                userEmail = null
            )
        }
        driveService = null
    }

    fun backup(fileName: String = "backup.json") = viewModelScope.launch {
        _state.update {
            it.copy(
                backupLoading = true
            )
        }
        val json = helper.backupRoomToJson()
        backupFile(fileName, json)
        updateBackupInfo()

        _state.update { it.copy(backupLoading = false) }
        ToastManager.show("Your data has been backed up.")
    }

    fun restore(fileName: String = "backup.json") = viewModelScope.launch {
        _state.update {
            it.copy(
                restoreLoading = true
            )
        }
        restoreFile(fileName)?.let { helper.restoreJsonToRoom(it) }
        updateBackupInfo()

        _state.update { it.copy(restoreLoading = false) }
        ToastManager.show("Your data has been restored.")
    }

    suspend fun backupFile(fileName: String, fileContent: String) =
        withContext(Dispatchers.IO) {
            val metadata = File().apply {
                name = fileName
                parents = listOf("appDataFolder")
            }
            val content = ByteArrayContent.fromString("application/json", fileContent)
            driveService?.files()?.create(metadata, content)?.execute()
        }


    suspend fun restoreFile(fileName: String): String? =
        withContext(Dispatchers.IO) {
            val result = driveService?.files()?.list()
                ?.setSpaces("appDataFolder")
                ?.setQ("name='$fileName'")
                ?.execute()

            val fileId = result?.files?.firstOrNull()?.id ?: return@withContext null
            val out = ByteArrayOutputStream()
            driveService?.files()?.get(fileId)?.executeMediaAndDownloadTo(out)
            out.toString()
        }
}
