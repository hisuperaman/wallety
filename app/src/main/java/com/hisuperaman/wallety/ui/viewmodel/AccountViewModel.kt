package com.hisuperaman.wallety.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisuperaman.wallety.data.database.AccountRepository
import com.hisuperaman.wallety.data.model.Account
import com.hisuperaman.wallety.ui.components.ToastManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AccountViewModel @Inject constructor (
    private val repository: AccountRepository
) : ViewModel() {
    private val _state = MutableStateFlow(AccountState())

    private val account: StateFlow<Account?> = repository.account
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )
    val state = combine(_state, account) { state, account ->
        state.copy(
            account = account
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AccountState())

    init {
        viewModelScope.launch {
            if (account.value != null)
            _state.update { it.copy(balance = account.value!!.balance.toString()) }
        }
    }

    fun onEvent(event: AccountEvent) {
        when (event) {
            AccountEvent.ShowDialog -> showDialog()
            AccountEvent.HideDialog -> hideDialog()
            is AccountEvent.SetBalance -> setBalance(event.balance)
            is AccountEvent.SaveAccount -> saveAccount(event.balance)
        }
    }

    private fun showDialog() {
        val acc = account.value ?: return

        _state.update {
            it.copy(
                isEditingBalance = true,
                balance = acc.balance.toString()
            )
        }
    }

    private fun hideDialog() {
        _state.update {
            it.copy(
                isEditingBalance = false
            )
        }
    }

    private fun setBalance(balance: String) {
        // TODO: make balance and amount with decimals
        val newBalance = balance.toLongOrNull()
        when {
            newBalance == null -> ToastManager.show("Please enter a valid number")
            newBalance < 0 -> ToastManager.show("Balance cannot be negative")
            newBalance.toString().length > 9 -> ToastManager.show("Balance too large (max 9 digits)")
            else -> {
                _state.update {
                    it.copy(
                        balance = balance
                    )
                }
            }
        }
    }

    private fun saveAccount(balance: String) {
        // TODO: make balance and amount with decimals
        val newBalance = balance.toLongOrNull()
        when {
            newBalance == null -> ToastManager.show("Please enter a valid number")
            newBalance < 0 -> ToastManager.show("Balance cannot be negative")
            newBalance.toString().length > 9 -> ToastManager.show("Balance too large (max 9 digits)")
            else -> {
                val acc = account.value ?: return
                viewModelScope.launch {
                    repository.upsertAccount(acc.copy(balance = balance.toLong()))
                }
            }
        }
    }
}