package com.hisuperaman.wallety.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hisuperaman.wallety.data.model.Account
import com.hisuperaman.wallety.data.model.Transaction


@Database(
    entities = [Account::class, Transaction::class],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
}