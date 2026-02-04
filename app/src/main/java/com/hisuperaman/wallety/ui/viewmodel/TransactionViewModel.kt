package com.hisuperaman.wallety.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisuperaman.wallety.data.database.AccountRepository
import com.hisuperaman.wallety.data.database.TransactionRepository
import com.hisuperaman.wallety.data.model.Category
import com.hisuperaman.wallety.data.model.PaymentType
import com.hisuperaman.wallety.data.model.Transaction
import com.hisuperaman.wallety.data.model.TransactionSummary
import com.hisuperaman.wallety.data.model.TransactionType
import com.hisuperaman.wallety.ui.components.ToastManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar
import javax.inject.Inject


@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val accountRepository: AccountRepository
): ViewModel() {
    private val _state = MutableStateFlow(TransactionState())
    private val latestTransactions = repository.getLatestTransactions()
    private val yearRange = repository.getYearRange()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<TransactionState> =
        combine(
            _state,
            latestTransactions,
            yearRange
        ) { state, latest, range ->
            Triple(state, latest, range)
        }
            .flatMapLatest { (state, latest, range) ->
                val monthStr = state.filterMonth.toString().padStart(2,'0')
                val yearStr = state.filterYear.toString()

                repository.getFilteredTransactions(monthStr, yearStr, state.filterType)
                    .map { filtered ->
                        val summary = getTransactionSummary(filtered)
                        state.copy(
                            transactions = filtered,
                            latestTransactions = latest,
                            minYear = range.minYear,
                            maxYear = range.maxYear,
                            summary = summary
                        )
                    }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                _state.value
            )


    fun onEvent(event: TransactionEvent) {
        when(event) {
            is TransactionEvent.SetAmount -> setAmount(event.amount)
            is TransactionEvent.SetCategory -> setCategory(event.category)
            is TransactionEvent.SetComment -> setComment(event.comment)
            is TransactionEvent.SetDate -> setDate(event.date)
            is TransactionEvent.SetPaymentType -> setPaymentType(event.paymentType)
            is TransactionEvent.SetType -> setType(event.type)

            is TransactionEvent.ShowDialog -> showDialog(event.type, event.transaction)
            TransactionEvent.HideDialog -> hideDialog()

            TransactionEvent.SaveTransaction -> saveTransaction()
            TransactionEvent.DeleteTransaction -> deleteTransaction()

            TransactionEvent.ShowCommentDialog -> showCommentDialog()
            TransactionEvent.HideCommentDialog -> hideCommentDialog()
            TransactionEvent.ShowDateDialog -> showDateDialog()
            TransactionEvent.HideDateDialog -> hideDateDialog()

            is TransactionEvent.AddDigitToAmount -> addDigitToAmount(event.digit)
            TransactionEvent.RemoveLastDigitFromAmount -> removeLastDigitFromAmount()

            TransactionEvent.ShowDeleteDialog -> showDeleteDialog()
            TransactionEvent.HideDeleteDialog -> hideDeleteDialog()

            is TransactionEvent.SetFilterType -> setFilterType(event.type)
            is TransactionEvent.SetFilterMonth -> setFilterMonth(event.month)
            is TransactionEvent.SetFilterYear -> setFilterYear(event.year)
        }
    }

    private fun getTransactionSummary(transactions: List<Transaction>): TransactionSummary {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfWeek = cal.timeInMillis
        cal.add(Calendar.DAY_OF_WEEK, 7)
        val endOfWeek = cal.timeInMillis

        val todayCal = Calendar.getInstance()
        todayCal.set(Calendar.HOUR_OF_DAY, 0)
        todayCal.set(Calendar.MINUTE, 0)
        todayCal.set(Calendar.SECOND, 0)
        todayCal.set(Calendar.MILLISECOND, 0)
        val startOfDay = todayCal.timeInMillis
        todayCal.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = todayCal.timeInMillis


        val incomeTransactions = transactions.filter { it.type == TransactionType.INCOME }
        val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }
        val incomeAmount = incomeTransactions.sumOf { it.amount }
        val expenseAmount = expenseTransactions.sumOf { it.amount }
        val monthlyAmount = incomeAmount - expenseAmount
        val weeklyIncomeTransactions = incomeTransactions.filter { it.date in startOfWeek until endOfWeek }
        val weeklyExpenseTransactions = expenseTransactions.filter { it.date in startOfWeek until endOfWeek }
        val weeklyIncomeAmount = weeklyIncomeTransactions.sumOf { it.amount }
        val weeklyExpenseAmount = weeklyExpenseTransactions.sumOf { it.amount }
        val weeklyAmount = weeklyIncomeAmount - weeklyExpenseAmount
        val dayIncomeTransactions = weeklyIncomeTransactions.filter { it.date in startOfDay until endOfDay }
        val dayExpenseTransactions = weeklyExpenseTransactions.filter { it.date in startOfDay until endOfDay }
        val dayIncomeAmount = dayIncomeTransactions.sumOf { it.amount }
        val dayExpenseAmount = dayExpenseTransactions.sumOf { it.amount }
        val dayAmount = dayIncomeAmount - dayExpenseAmount

        return TransactionSummary(monthlyAmount, weeklyAmount, dayAmount)
    }

    private fun setFilterType(type: TransactionType?) {
        _state.update {
            it.copy(
                filterType = if (it.filterType==type) null else type
            )
        }
    }

    private fun setFilterMonth(month: Int) {
        _state.update {
            it.copy(
                filterMonth = month
            )
        }
    }

    private fun setFilterYear(year: Int) {
        _state.update {
            it.copy(
                filterYear = year
            )
        }
    }

    private fun showDialog(type: TransactionType, transaction: Transaction?) {
        _state.update {
            it.copy(
                isDialogVisible = true,
                type = type,
                editingTransaction = transaction,

                amount = transaction?.amount?.toString() ?: "",
                comment = transaction?.comment ?: "",
                paymentType = transaction?.paymentType ?: PaymentType.CASH,
                category = transaction?.category ?: Category.INCOME,
                date = transaction?.date ?: System.currentTimeMillis()
            )
        }
    }

    private fun hideDialog() {
        _state.update {
            it.copy(
                isDialogVisible = false,
                editingTransaction = null
            )
        }
    }

    private fun showCommentDialog() {
        _state.update {
            it.copy(
                isCommentDialogVisible = true,
            )
        }
    }

    private fun hideCommentDialog() {
        _state.update {
            it.copy(
                isCommentDialogVisible = false
            )
        }
    }

    private fun showDateDialog() {
        _state.update {
            it.copy(
                isDateDialogVisible = true
            )
        }
    }

    private fun hideDateDialog() {
        _state.update {
            it.copy(
                isDateDialogVisible = false
            )
        }
    }

    private fun showDeleteDialog() {
        _state.update {
            it.copy(
                isDeleteDialogVisible = true
            )
        }
    }

    private fun hideDeleteDialog() {
        _state.update {
            it.copy(
                isDeleteDialogVisible = false
            )
        }
    }

    private fun setAmount(amount: String) {
        _state.update {
            it.copy(
                amount = amount
            )
        }
    }

    private fun setCategory(category: Category) {
        _state.update {
            it.copy(
                category = category
            )
        }
    }

    private fun setComment(comment: String) {
        _state.update {
            it.copy(
                comment = comment
            )
        }
    }

    private fun setDate(date: Long?) {
        _state.update {
            it.copy(
                date = date
            )
        }
    }

    private fun setPaymentType(paymentType: PaymentType) {
        _state.update {
            it.copy(
                paymentType = paymentType
            )
        }
    }

    private fun setType(type: TransactionType) {
        _state.update {
            it.copy(
                type = type
            )
        }
    }

    private fun addDigitToAmount(digit: String) {
        if (_state.value.amount.length>=9) return

        _state.update {
            it.copy(
                amount = it.amount+digit
            )
        }
    }

    private fun removeLastDigitFromAmount() {
        _state.update {
            it.copy(
                amount = it.amount.dropLast(1)
            )
        }
    }

    private fun deleteTransaction() {
        val transaction = _state.value.editingTransaction
        if (transaction===null) return
        viewModelScope.launch {
            accountRepository.account.firstOrNull()?.let { account ->
                val signedAmount = if (transaction.type == TransactionType.INCOME) transaction.amount else -(transaction.amount)
                accountRepository.upsertAccount(account.copy(
                    balance = account.balance - signedAmount
                ))
            }

            repository.deleteTransaction(transaction)
        }
    }

    private fun saveTransaction() {
        val stateValue = _state.value
        if (stateValue.amount.isBlank() || stateValue.amount.toLongOrNull() == null || stateValue.date == null) return

        val transaction = _state.value.editingTransaction
        val now = System.currentTimeMillis()
        if (transaction == null) {
            viewModelScope.launch {
                val account = accountRepository.account.firstOrNull() ?: return@launch

                val signedAmount = if (stateValue.type == TransactionType.INCOME) stateValue.amount.toLong() else -(stateValue.amount.toLong())
                val newBalance = account.balance+signedAmount
                // TODO: make balance and amount with decimals
                when {
                    newBalance < 0 -> ToastManager.show("Insufficient balance")
                    newBalance.toString().length > 9 -> ToastManager.show("Amount is too large")
                    else -> {
                        accountRepository.upsertAccount(account.copy(
                            balance = newBalance
                        ))

                        repository.upsertTransaction(Transaction(
                            amount = stateValue.amount.toLong(),
                            type = stateValue.type,
                            comment = stateValue.comment,
                            paymentType = stateValue.paymentType,
                            category = stateValue.category,
                            date = stateValue.date,
                            createdAt = now,
                            updatedAt = now
                        ))
                    }
                }
            }
        }
        else {
            viewModelScope.launch {
                val account = accountRepository.account.firstOrNull() ?: return@launch

                val oldSigned = if (transaction.type == TransactionType.INCOME) transaction.amount else -transaction.amount
                val newSigned = if (stateValue.type == TransactionType.INCOME) stateValue.amount.toLong() else -stateValue.amount.toLong()

                val signedAmount = newSigned - oldSigned
                val newBalance = account.balance+signedAmount
                // TODO: make balance and amount with decimals
                when {
                    newBalance < 0 -> ToastManager.show("Insufficient balance")
                    newBalance.toString().length > 9 -> ToastManager.show("Amount is too large")
                    else -> {
                        accountRepository.upsertAccount(account.copy(
                            balance = newBalance
                        ))

                        repository.upsertTransaction(
                            transaction.copy(
                                amount = stateValue.amount.toLong(),
                                type = stateValue.type,
                                comment = stateValue.comment,
                                paymentType = stateValue.paymentType,
                                category = stateValue.category,
                                date = stateValue.date,
                                updatedAt = now
                            )
                        )
                    }
                }
            }
        }
    }
}