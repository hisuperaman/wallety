package com.hisuperaman.wallety.ui.viewmodel

import com.hisuperaman.wallety.data.model.Category
import com.hisuperaman.wallety.data.model.PaymentType
import com.hisuperaman.wallety.data.model.Transaction
import com.hisuperaman.wallety.data.model.TransactionSummary
import com.hisuperaman.wallety.data.model.TransactionType
import java.util.Calendar


private val calendar = Calendar.getInstance()
private val currentMonth = calendar.get(Calendar.MONTH) + 1
private val currentYear = calendar.get(Calendar.YEAR)

data class TransactionState(
    val amount: String = "",
    val type: TransactionType = TransactionType.INCOME,
    val comment: String = "",
    val paymentType: PaymentType = PaymentType.CASH,
    val category: Category = Category.INCOME,
    val date: Long? = null,

    val isCommentDialogVisible: Boolean = false,
    val isDateDialogVisible: Boolean = false,
    val isDeleteDialogVisible: Boolean = false,

    val isDialogVisible: Boolean = false,
    val editingTransaction: Transaction? = null,

    val transactions: List<Transaction> = listOf<Transaction>(),
    val latestTransactions: List<Transaction> = listOf<Transaction>(),

    val filterType: TransactionType? = null,
    val filterMonth: Int = currentMonth,
    val filterYear: Int = currentYear,
    val minYear: Int = currentYear,
    val maxYear: Int = currentYear,

    val summary: TransactionSummary = TransactionSummary(0, 0, 0),

    val expensePercentChange: Double = 100.0
)