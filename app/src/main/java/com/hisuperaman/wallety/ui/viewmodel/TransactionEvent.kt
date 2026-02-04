package com.hisuperaman.wallety.ui.viewmodel

import com.hisuperaman.wallety.data.model.Category
import com.hisuperaman.wallety.data.model.PaymentType
import com.hisuperaman.wallety.data.model.Transaction
import com.hisuperaman.wallety.data.model.TransactionType


sealed interface TransactionEvent {
    object SaveTransaction : TransactionEvent
    data class SetAmount(val amount: String) : TransactionEvent
    data class SetType(val type: TransactionType) : TransactionEvent
    data class SetComment(val comment: String) : TransactionEvent
    data class SetPaymentType(val paymentType: PaymentType) : TransactionEvent
    data class SetCategory(val category: Category) : TransactionEvent
    data class SetDate(val date: Long?) : TransactionEvent
    object DeleteTransaction : TransactionEvent

    object ShowCommentDialog: TransactionEvent
    object HideCommentDialog: TransactionEvent
    object ShowDateDialog: TransactionEvent
    object HideDateDialog: TransactionEvent
    object ShowDeleteDialog: TransactionEvent
    object HideDeleteDialog: TransactionEvent

    data class ShowDialog(val type: TransactionType, val transaction: Transaction? = null): TransactionEvent
    object HideDialog: TransactionEvent

    data class AddDigitToAmount(val digit: String): TransactionEvent
    object RemoveLastDigitFromAmount: TransactionEvent

    data class SetFilterType(val type: TransactionType?) : TransactionEvent
    data class SetFilterMonth(val month: Int) : TransactionEvent
    data class SetFilterYear(val year: Int) : TransactionEvent
}