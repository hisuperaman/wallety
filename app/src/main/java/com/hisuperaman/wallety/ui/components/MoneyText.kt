package com.hisuperaman.wallety.ui.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.hisuperaman.wallety.data.model.TransactionType
import com.hisuperaman.wallety.ui.theme.AdaptiveGreen
import com.hisuperaman.wallety.ui.theme.AdaptiveRed
import com.hisuperaman.wallety.ui.theme.SoftGreen
import com.hisuperaman.wallety.ui.theme.SoftPeach
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MoneyText(
    amount: Long,
    modifier: Modifier = Modifier,
    transactionType: TransactionType? = null,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    fontWeight: FontWeight = MaterialTheme.typography.titleMedium.fontWeight ?: FontWeight.Normal
) {
    val formatter = NumberFormat.getCurrencyInstance()
    val formattedAmount = formatter.format(amount)

    val color = when (transactionType) {
        TransactionType.INCOME -> AdaptiveGreen
        TransactionType.EXPENSE -> AdaptiveRed
        null -> MaterialTheme.colorScheme.primary
    }
    val transactionTypeSign = when (transactionType) {
        TransactionType.INCOME -> "+"
        TransactionType.EXPENSE -> "-"
        null -> ""
    }

    BasicText(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium)) {
                append(transactionTypeSign)
            }
            append(formattedAmount)
        },
        style = style.copy(
            color = color,
            fontWeight = fontWeight
        ),
        modifier = modifier,
        softWrap = false,
        autoSize = TextAutoSize.StepBased(
            minFontSize = 12.sp,
            maxFontSize = style.fontSize
        )
    )
}