package com.hisuperaman.wallety.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hisuperaman.wallety.data.model.Account
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Upsert
    suspend fun upsertAccount(account: Account)

    @Query("SELECT * FROM account ORDER BY createdAt DESC LIMIT 1")
    fun getAccount(): Flow<Account?>

    @Query("DELETE FROM `account`")
    suspend fun clearAll()

    @Query("SELECT * FROM account")
    suspend fun getAll(): List<Account>
}