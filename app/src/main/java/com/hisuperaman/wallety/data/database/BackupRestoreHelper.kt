package com.hisuperaman.wallety.data.database

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hisuperaman.wallety.data.model.Account
import com.hisuperaman.wallety.data.model.Transaction
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


data class BackupData(
    val accounts: List<Account>,
    val transactions: List<Transaction>
)

class BackupRestoreHelper @Inject constructor(
    private val db: AppDatabase
) {

    suspend fun backupRoomToJson(): String = withContext(Dispatchers.IO) {
        val accounts = db.accountDao().getAll()
        val transactions = db.transactionDao().getAll()
        val backupData = BackupData(accounts, transactions)
        Gson().toJson(backupData)
    }


    suspend fun restoreJsonToRoom(json: String) = withContext(Dispatchers.IO) {
        val type = object : TypeToken<BackupData>() {}.type
        val backupData: BackupData = Gson().fromJson(json, type)

        db.accountDao().clearAll()
        backupData.accounts.forEach { db.accountDao().upsertAccount(it) }

        db.transactionDao().clearAll()
        db.transactionDao().insertAll(backupData.transactions)
    }
}
