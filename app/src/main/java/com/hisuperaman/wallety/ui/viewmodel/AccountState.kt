package com.hisuperaman.wallety.ui.viewmodel

import com.hisuperaman.wallety.data.model.Account

data class AccountState(
    val account: Account? = null,
    val balance: String = "0",
    val isEditingBalance: Boolean = false
)