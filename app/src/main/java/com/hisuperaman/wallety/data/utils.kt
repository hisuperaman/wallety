package com.hisuperaman.wallety.data

import com.hisuperaman.wallety.data.model.Transaction
import com.hisuperaman.wallety.data.model.TransactionType
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.YearMonth
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs


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
            .sumOf { it.amount.toRupees() }
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


fun getFormattedRupees(paise: Long): String {
    val rupees = paise / 100
    val remainingPaise = paise % 100
    return "₹$rupees.${remainingPaise.toString().padStart(2, '0')}"
}

fun Long.toRupees(): Double = this / 100.0

fun Double.toRupeesString(): String {
    return if (this % 1.0 == 0.0) {
        this.toInt().toString()
    } else {
        String.format(Locale.US, "%.2f", this)
    }
}

fun String.toPaise(): Long {
    val parts = this.trim().split(".")
    val rupees = parts.getOrNull(0)?.toLongOrNull() ?: 0L
    val paise = parts.getOrNull(1)
        ?.padEnd(2, '0')
        ?.take(2)
        ?.toLongOrNull() ?: 0L
    return rupees * 100 + paise
}

val moneyRegex = Regex("""^\d+(\.\d{1,2})?$""")

fun String.isValidMoney(): Boolean = moneyRegex.matches(this)

fun String.toRupees(): String {
    val paise = this.toLongOrNull() ?: return "0.00"
    val rupees = paise / 100
    val remainingPaise = paise % 100
    return "$rupees.${remainingPaise.toString().padStart(2, '0')}"
}

const val MAX_COMMENT_LENGTH = 80

fun calculateInitialDelay(hour: Int, minute: Int): Long {
    val now = Calendar.getInstance()

    val target = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)

        if (before(now)) {
            add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    return target.timeInMillis - now.timeInMillis
}

fun getPercentChange(thisMonth: Long?, lastMonth: Long?): Double {
    val t = thisMonth ?: 0
    val l = lastMonth ?: 0
    if (l == 0L) return 100.0
    return ((t - l).toDouble() / l) * 100
}

fun getFormattedExpensePercent(percent: Double): String {
    val sign = if (percent >= 0) "-" else "+"
    return String.format(Locale.US, "%s%.2f%%", sign, abs(percent))
}
