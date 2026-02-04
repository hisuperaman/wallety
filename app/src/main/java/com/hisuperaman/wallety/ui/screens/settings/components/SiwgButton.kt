package com.hisuperaman.wallety.ui.screens.settings.components

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.hisuperaman.wallety.R
import kotlinx.coroutines.launch
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import com.hisuperaman.wallety.ui.components.ToastManager
import com.hisuperaman.wallety.ui.viewmodel.DriveEvent
import com.hisuperaman.wallety.ui.viewmodel.DriveState
import com.hisuperaman.wallety.ui.viewmodel.DriveViewModel


@Composable
fun SiwgButton(
    webClientId: String,
    onEvent: (DriveEvent) -> Unit,
    state: DriveState
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            onEvent(DriveEvent.SignUp(account, context))
            ToastManager.show("Signed in as ${account.email}")
        } catch (e: ApiException) {
            ToastManager.show("Sign-in failed")
            onEvent(DriveEvent.SignUpFail)
        }
    }

    SettingsItem(
        title = stringResource(R.string.google_account),
        description = state.userEmail ?: "Please select a Google Account",
        onClick = {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(webClientId)
                .requestScopes(Scope(DriveScopes.DRIVE_APPDATA))
                .build()
            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            coroutineScope.launch {
                googleSignInClient.signOut().addOnCompleteListener {
                    launcher.launch(googleSignInClient.signInIntent)
                }
            }
        }
    )
}