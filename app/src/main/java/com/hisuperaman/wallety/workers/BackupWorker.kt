package com.hisuperaman.wallety.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.hisuperaman.wallety.data.database.AppDatabase
import com.hisuperaman.wallety.data.database.BackupHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.IOException

@HiltWorker
class BackupWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val db: AppDatabase
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val account = GoogleSignIn.getLastSignedInAccount(applicationContext)?.account
            ?: return fail("No signed-in Google account")

        val credential = GoogleAccountCredential.usingOAuth2(
            applicationContext,
            listOf(DriveScopes.DRIVE_APPDATA)
        )
        credential.selectedAccount = account
        val drive = Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("Wallety").build()

        return try {
            BackupHelper(db, drive).runBackup()
            Log.i(TAG, "Backup completed")
            Result.success()
        } catch (e: UserRecoverableAuthIOException) {
            // Drive consent was revoked or never granted; needs user interaction in the app
            fail("Drive authorization required", e)
        } catch (e: GoogleAuthException) {
            fail("Google authentication failed", e)
        } catch (e: GoogleJsonResponseException) {
            if (e.statusCode == 429 || e.statusCode >= 500) retry("Drive API error ${e.statusCode}", e)
            else fail("Drive API error ${e.statusCode}: ${e.details?.message}", e)
        } catch (e: IOException) {
            retry("Network error", e)
        } catch (e: Exception) {
            fail("Unexpected error", e)
        }
    }

    private fun retry(message: String, e: Throwable): Result {
        if (runAttemptCount >= MAX_ATTEMPTS) return fail("$message (giving up after $runAttemptCount attempts)", e)
        Log.w(TAG, "$message, retrying (attempt ${runAttemptCount + 1})", e)
        return Result.retry()
    }

    private fun fail(message: String, e: Throwable? = null): Result {
        Log.e(TAG, "Backup failed: $message", e)
        return Result.failure(workDataOf(KEY_ERROR to (e?.let { "$message: ${it.message}" } ?: message)))
    }

    companion object {
        private const val TAG = "BackupWorker"
        private const val MAX_ATTEMPTS = 5
        const val KEY_ERROR = "error"
    }
}
