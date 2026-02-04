package com.hisuperaman.wallety.ui.viewmodel

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.hisuperaman.wallety.data.model.Category
import com.hisuperaman.wallety.data.model.PaymentType
import com.hisuperaman.wallety.data.model.Transaction
import com.hisuperaman.wallety.data.model.TransactionType


sealed interface DriveEvent {
    data class SignUp(val account: GoogleSignInAccount, val context: Context) : DriveEvent
    object Backup : DriveEvent
    object Restore : DriveEvent
    object SignUpFail : DriveEvent
    object ShowBackupDialog : DriveEvent
    object HideBackupDialog : DriveEvent
    object ShowRestoreDialog : DriveEvent
    object HideRestoreDialog : DriveEvent
    object ShowAutoBackupDialog : DriveEvent
    object HideAutoBackupDialog : DriveEvent
}