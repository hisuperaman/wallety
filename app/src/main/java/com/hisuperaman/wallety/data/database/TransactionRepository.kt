package com.hisuperaman.wallety.data.database

import com.hisuperaman.wallety.data.model.MonthTotals
import com.hisuperaman.wallety.data.model.Transaction
import com.hisuperaman.wallety.data.model.TransactionType
import com.hisuperaman.wallety.data.model.YearRange
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject

class TransactionRepository @Inject constructor(private val transactionDao: TransactionDao) {
    val calendar: Calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH) + 1
    val currentYear = calendar.get(Calendar.YEAR)
    val monthStr = currentMonth.toString().padStart(2, '0')
    val yearStr = currentYear.toString()

    fun getLatestTransactions(limit: Int=5, month: String=monthStr): Flow<List<Transaction>> {
        return transactionDao.getLatestTransactions(limit, month)
    }

    suspend fun upsertTransaction(transaction: Transaction) {
        transactionDao.upsertTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }

    fun getFilteredTransactions(month: String, year: String, type: TransactionType?): Flow<List<Transaction>> {
        return transactionDao.getFilteredTransactions(month, year, type)
    }

    fun getYearRange(): Flow<YearRange> {
        return transactionDao.getYearRange()
    }

    fun getMonthTotals(transactionType: TransactionType): Flow<MonthTotals> {
        return transactionDao.getMonthTotals(transactionType)
    }
}