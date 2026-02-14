package com.hisuperaman.wallety.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.hisuperaman.wallety.data.model.MonthTotals
import com.hisuperaman.wallety.data.model.Transaction
import com.hisuperaman.wallety.data.model.TransactionType
import com.hisuperaman.wallety.data.model.YearRange
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Upsert
    suspend fun upsertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM `transaction` WHERE strftime('%m', datetime(date / 1000, 'unixepoch')) = :month ORDER BY `date` DESC LIMIT :limit")
    fun getLatestTransactions(limit: Int, month: String): Flow<List<Transaction>>

    @Query("""
        SELECT * FROM `transaction`
        WHERE strftime('%m', datetime(date / 1000, 'unixepoch')) = :month
        AND strftime('%Y', datetime(date / 1000, 'unixepoch')) = :year
        AND (:type IS NULL OR type = :type)
        ORDER BY `date` DESC
    """)
    fun getFilteredTransactions(month: String, year: String, type: TransactionType?): Flow<List<Transaction>>

    @Query("""
        SELECT 
            MIN(strftime('%Y', datetime(date / 1000, 'unixepoch'))) AS minYear,
            MAX(strftime('%Y', datetime(date / 1000, 'unixepoch'))) AS maxYear
        FROM `transaction`
    """)
    fun getYearRange(): Flow<YearRange>

    @Query("SELECT * FROM `transaction`")
    suspend fun getAll(): List<Transaction>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<Transaction>)

    @Query("DELETE FROM `transaction`")
    suspend fun clearAll()

    @Query("""
SELECT 
  SUM(CASE 
        WHEN strftime('%m', date/1000, 'unixepoch') = strftime('%m', 'now')
         AND strftime('%Y', date/1000, 'unixepoch') = strftime('%Y', 'now')
        THEN amount ELSE 0 END) AS thisMonth,

  SUM(CASE 
        WHEN strftime('%m', date/1000, 'unixepoch') = strftime('%m', 'now','-1 month')
         AND strftime('%Y', date/1000, 'unixepoch') = strftime('%Y', 'now','-1 month')
        THEN amount ELSE 0 END) AS lastMonth
FROM `transaction`
WHERE type = :type
""")
    fun getMonthTotals(type: TransactionType): Flow<MonthTotals>
}