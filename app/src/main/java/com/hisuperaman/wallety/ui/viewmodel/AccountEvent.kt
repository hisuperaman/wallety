package com.hisuperaman.wallety.ui.viewmodel

sealed interface AccountEvent {
    data class SaveAccount(val balance: String) : AccountEvent
    data class SetBalance(val balance: String) : AccountEvent
    object ShowDialog : AccountEvent
    object HideDialog : AccountEvent
}