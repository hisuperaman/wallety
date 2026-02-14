package com.hisuperaman.wallety.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.hisuperaman.wallety.data.database.AppDatabase
import com.hisuperaman.wallety.data.database.BackupHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class BackupWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val db: AppDatabase
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        if (runAttemptCount >= 20) return Result.failure()
        val account = GoogleSignIn.getLastSignedInAccount(applicationContext)
            ?: return Result.retry()

        val credential = GoogleAccountCredential.usingOAuth2(
            applicationContext,
            listOf(DriveScopes.DRIVE_APPDATA)
        )
        credential.selectedAccount = account.account
        val applicationContext = Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("Wallety").build()

        val helper = BackupHelper(db, applicationContext)

        return try {
            helper.runBackup()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
