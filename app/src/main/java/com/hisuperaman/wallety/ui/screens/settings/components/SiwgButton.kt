package com.hisuperaman.wallety.ui.screens.settings.components

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import com.hisuperaman.wallety.R
import com.hisuperaman.wallety.ui.components.ToastManager
import com.hisuperaman.wallety.ui.viewmodel.DriveEvent
import com.hisuperaman.wallety.ui.viewmodel.DriveState
import kotlinx.coroutines.launch


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
            Log.e("error", "Sign-in failed: ${e.statusCode} - ${e.message}")
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