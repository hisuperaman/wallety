package com.hisuperaman.wallety.data.database

import com.google.api.client.http.ByteArrayContent
import com.google.api.services.drive.Drive
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BackupHelper(
    private val db: AppDatabase,
    private val drive: Drive
) {

    suspend fun runBackup(fileName: String = "backup.json") {
        val json = backupRoomToJson()
        backupFile(fileName, json)
    }

    private suspend fun backupFile(fileName: String, fileContent: String) =
        withContext(Dispatchers.IO) {
            val metadata = com.google.api.services.drive.model.File().apply {
                name = fileName
                parents = listOf("appDataFolder")
            }

            val content = ByteArrayContent.fromString("application/json", fileContent)
            drive.files().create(metadata, content).execute()
        }

    private suspend fun backupRoomToJson(): String = withContext(Dispatchers.IO) {
        val accounts = db.accountDao().getAll()
        val transactions = db.transactionDao().getAll()
        Gson().toJson(BackupData(accounts, transactions))
    }
}
