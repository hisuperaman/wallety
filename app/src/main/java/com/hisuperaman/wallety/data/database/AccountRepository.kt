package com.hisuperaman.wallety.data.database

import com.hisuperaman.wallety.data.model.Account
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class AccountRepository @Inject constructor(private val accountDao: AccountDao) {
    val account: Flow<Account?> = accountDao.getAccount()

    suspend fun upsertAccount(account: Account) {
        accountDao.upsertAccount(account)
    }
}