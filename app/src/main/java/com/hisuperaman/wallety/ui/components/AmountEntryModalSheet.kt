package com.hisuperaman.wallety.ui.components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hisuperaman.wallety.ui.viewmodel.TransactionEvent
import com.hisuperaman.wallety.ui.viewmodel.TransactionState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmountEntryModalSheet(
    transactionState: TransactionState,
    transactionOnEvent: (TransactionEvent) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if (transactionState.isDialogVisible) {
        ModalBottomSheet (
            onDismissRequest = { transactionOnEvent(TransactionEvent.HideDialog) },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            AmountEntrySheet(
                state = transactionState,
                onEvent = transactionOnEvent,
                modifier = Modifier.fillMaxHeight(0.75f)
            )
        }
    }
}