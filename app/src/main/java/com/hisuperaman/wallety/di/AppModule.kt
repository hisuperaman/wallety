package com.hisuperaman.wallety.di

import android.content.Context
import androidx.room.Room
import com.hisuperaman.wallety.data.database.AccountDao
import com.hisuperaman.wallety.data.database.AccountRepository
import com.hisuperaman.wallety.data.database.AppDatabase
import com.hisuperaman.wallety.data.database.TransactionDao
import com.hisuperaman.wallety.data.database.TransactionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "app_db")
            .fallbackToDestructiveMigration(false)
            .build()

    @Provides
    fun providesAccountDao(db: AppDatabase): AccountDao = db.accountDao()

    @Provides
    fun providesAccountRepository(dao: AccountDao): AccountRepository = AccountRepository(dao)

    @Provides
    fun providesTransactionDao(db: AppDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun providesTransactionRepository(dao: TransactionDao) = TransactionRepository(dao)
}