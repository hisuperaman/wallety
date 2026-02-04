package com.hisuperaman.wallety.data

import com.hisuperaman.wallety.data.model.Transaction
import com.hisuperaman.wallety.data.model.TransactionType
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.YearMonth
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone


fun getDaysInMonth(year: Int, month: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) 29 else 28
        else -> 30 // fallback, should not happen
    }
}

fun getDailyExpenses(transactions: List<Transaction>, year: Int, month: Int): List<Double> {
    val daysInMonth = getDaysInMonth(year, month)

    val dailyExpenses = (1..daysInMonth).map { day ->
        transactions
            .filter { tx ->
                val cal = Calendar.getInstance().apply { timeInMillis = tx.date }
                val txDay = cal.get(Calendar.DAY_OF_MONTH)
                tx.type == TransactionType.EXPENSE && txDay == day
            }
            .sumOf { it.amount.toDouble() }
    }
    return dailyExpenses
}

fun getFormattedTimestamp(rfc3339: String?): String {
    if (rfc3339 == null) return "N/A"
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")

    val date = sdf.parse(rfc3339) ?: return ""
    val cal = Calendar.getInstance()
    cal.time = date

    val out = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return out.format(cal.time)
}

fun formatFileSize(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"

    val kb = bytes / 1024.0
    if (kb < 1024) return "%.2f KB".format(kb)

    val mb = kb / 1024.0
    return "%.2f MB".format(mb)
}
