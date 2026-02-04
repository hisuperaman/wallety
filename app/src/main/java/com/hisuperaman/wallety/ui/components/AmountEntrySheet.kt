package com.hisuperaman.wallety.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hisuperaman.wallety.R
import com.hisuperaman.wallety.data.model.Category
import com.hisuperaman.wallety.data.model.PaymentType
import com.hisuperaman.wallety.ui.theme.SoftBlue
import com.hisuperaman.wallety.ui.viewmodel.TransactionEvent
import com.hisuperaman.wallety.ui.viewmodel.TransactionState
import java.util.Currency
import java.util.Locale


class CurrencyVisualTransformation(
    private val symbol: String
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }
//        val annotated = buildAnnotatedString {
//            withStyle(
//                SpanStyle(fontSize = 20.sp)
//            ) {
//                append(symbol)
//            }
//            withStyle(
//                SpanStyle(fontSize = 36.sp)
//            ) {
//                append(digits)
//            }
//        }
        val display = if (digits.isEmpty()) symbol else "$symbol$digits"
        val offsetMap = object : OffsetMapping {
            override fun originalToTransformed(offset: Int) =
                offset + symbol.length

            override fun transformedToOriginal(offset: Int) =
                (offset - symbol.length).coerceAtLeast(0)
        }
        return TransformedText(AnnotatedString(display), offsetMap)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    initialDateMillis: Long?,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(state.selectedDateMillis)
                onDismiss()
            }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    ) {
        DatePicker(state = state)
    }
}

@Composable
fun AmountEntrySheet(
    state: TransactionState,
    onEvent: (TransactionEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val currency = Currency.getInstance(Locale.getDefault())
    val symbol = currency.symbol

    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md)),
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_sm))
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xs)),
            modifier = Modifier
                .padding(paddingValues = PaddingValues(horizontal = dimensionResource(R.dimen.padding_md)))
                .height(56.dp)
        ) {
            SelectMenu(
                optionsWithIcon = PaymentType.entries.map { it.label to it.icon },
                selected = state.paymentType.label,
                onSelect = {label ->
                    val type = PaymentType.entries.first { it.label==label }
                    onEvent(TransactionEvent.SetPaymentType(type))
                },
                imageVector = state.paymentType.icon,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            SelectMenu(
                optionsWithIcon = Category.entries.map { it.label to it.icon },
                selected = state.category.label,
                onSelect = {label ->
                    val type = Category.entries.first { it.label==label }
                    onEvent(TransactionEvent.SetCategory(type))
                },
                color = SoftBlue,
                imageVector = state.category.icon,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentColor = Color.Black
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .height(40.dp)
                    .padding(horizontal = 16.dp)
            ) {
                TransactionTypeFilter(
                    selected = state.type,
                    onSelect = { onEvent(TransactionEvent.SetType(it)) }
                )
            }

            BasicTextField(
                value = state.amount,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                textStyle = MaterialTheme.typography.displayLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = CurrencyVisualTransformation(symbol),
                decorationBox = { innerTextField ->
                    if (state.amount.isEmpty()) {
                        Text(
                            "${symbol}0",
                            style = MaterialTheme.typography.displayLarge.copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                textAlign = TextAlign.Center
                            )
                        )
                    } else
                        innerTextField()
                }
            )

            NumericKeypad(
                onKeyClick = {
                    onEvent(TransactionEvent.AddDigitToAmount(it))
                },
                onBackspaceClick = {
                    onEvent(TransactionEvent.RemoveLastDigitFromAmount)
                },
                onDatePickerClick = {
                    onEvent(TransactionEvent.ShowDateDialog)
                },
                onCommentClick = {
                    onEvent(TransactionEvent.ShowCommentDialog)
                },
                onSaveClick = {
                    onEvent(TransactionEvent.SaveTransaction)
                    onEvent(TransactionEvent.HideDialog)
                },
                showDeleteKey = state.editingTransaction!==null,
                onDeleteClick = {
                    onEvent(TransactionEvent.ShowDeleteDialog)
                }
            )
        }
    }

    if (state.isDateDialogVisible) {
        DatePickerModal(
            initialDateMillis = state.date,
            onDateSelected = { onEvent(TransactionEvent.SetDate(it)) },
            onDismiss = { onEvent(TransactionEvent.HideDateDialog) }
        )
    }

    InputConfirmationDialog(
        showDialog = state.isCommentDialogVisible,
        title = "Comment",
        message = "Enter a comment:",
        inputText = state.comment,
        onConfirm = {
            onEvent(TransactionEvent.SetComment(it))
            onEvent(TransactionEvent.HideCommentDialog)
        },
        onDismiss = { onEvent(TransactionEvent.HideCommentDialog) }
    )

    ConfirmationDialog(
        showDialog = state.isDeleteDialogVisible,
        title = "Delete",
        message = "Are you sure you want to delete this transaction?",
        onConfirm = {
            onEvent(TransactionEvent.DeleteTransaction)
            onEvent(TransactionEvent.HideDeleteDialog)
            onEvent(TransactionEvent.HideDialog)
        },
        onDismiss = { onEvent(TransactionEvent.HideDeleteDialog) }
    )
}
